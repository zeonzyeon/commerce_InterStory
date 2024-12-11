package com.app.interstory.user.service;

import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.app.interstory.common.service.S3Service;
import com.app.interstory.config.FilePathConfig;
import com.app.interstory.config.globalExeption.customException.DuplicateEmailException;
import com.app.interstory.config.globalExeption.customException.DuplicateNicknameException;
import com.app.interstory.config.globalExeption.customException.WrongApproach;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.Social;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.domain.enumtypes.Roles;
import com.app.interstory.user.dto.request.LocalSignUpRequest;
import com.app.interstory.user.dto.request.withdrawalRequest;
import com.app.interstory.user.dto.response.UserResponse;
import com.app.interstory.user.dto.vo.KakaoAPI;
import com.app.interstory.user.dto.vo.KakaoUserInfo;
import com.app.interstory.user.repository.UserRepository;
import com.app.interstory.util.Utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final KakaoAPI kakaoAPI;
    private final FilePathConfig filePathConfig;
    private final S3Service s3Service;


    public boolean verifyNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    public boolean verifyEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Transactional
    public UserResponse localSingUp(LocalSignUpRequest localSignUpRequest) {
        if (!verifyEmail(localSignUpRequest.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (!verifyNickname(localSignUpRequest.getNickname())) {
            throw new DuplicateNicknameException("이미 존재하는 닉네임입니다.");
        }

        User savedUser = userRepository.save(localSignUpRequestToUser(localSignUpRequest));

        return UserResponse.userToUserResponse(savedUser);
    }

    private User createKaKaoUser(KakaoUserInfo userInfo) {
        userInfo.addAuthProvider(kakaoAPI.getAuthProvider());
        User user = socialProviderSignToUser(userInfo);
        Social social = createSocialProvider(user,userInfo);
        user.addSocialProvider(social);
        return userRepository.save(user);
    }


    //소셜 로그인 유저 확인 및 저장
    @Transactional
    public User findByUserOrSave(KakaoUserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getId()+kakaoAPI.getKAKAO_USER())
                .orElseGet(()->createKaKaoUser(userInfo));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
    }

    //회원 삭제
    @Transactional
    public UserResponse withdrawal(withdrawalRequest request, CustomUserDetails currentUser) {
        //본인이 아니거나, 관리자가 아니면 에러
        checkIdentity(request,currentUser);
        //s3 이미지 삭제 및 소셜 삭제
        User withdrawalUser = deleteUserRelatedData(currentUser);
        //회원 탈퇴 설정
        withdrawalUser.withdrawal(request);

        SecurityContextHolder.clearContext();

        return UserResponse.userToUserResponse(withdrawalUser);
    }

    //s3 이미지 삭제 및 소셜 삭제 탈퇴
    private User deleteUserRelatedData(CustomUserDetails currentUser){
        User withdrawalUser = userRepository.findByIdWithSocial(currentUser.getUser().getUserId())
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if(withdrawalUser.getSocial() != null){
            String unlinkUrl = kakaoAPI.getKAKAO_USER_WITHDRAWAL_URL();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoAPI.getAdminKey());
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("target_id_type","user_id");
            params.add("target_id",withdrawalUser.getSocial().getClientId());

            HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(params, headers);

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.postForObject(unlinkUrl, request, KakaoUserInfo.class);
            } catch (RestClientException e) {
                log.error("Kakao unlink failed: {}", e.getMessage());
                throw new IllegalArgumentException("카카오 연동 해제 실패", e);
            }
            withdrawalUser.deleteSocial();
        }
        // 기본 이미지 아닐 시 s3에 저장된 이미지 삭제
        if(!withdrawalUser.isDefaultProfile()){
            s3Service.deleteFile(withdrawalUser.getProfileUrl());
        }

        return withdrawalUser;
    }

    private void checkIdentity(withdrawalRequest request, CustomUserDetails currentUser) {
        boolean isOwner = Objects.equals(request.getUserId(), currentUser.getUser().getUserId());
        boolean isAdmin = currentUser.getUser().getRole().equals(Roles.ADMIN);

        if(!isOwner && !isAdmin){
            throw new WrongApproach("잘못된 접근 : 계정을 확인하세요.");
        }
    }

    //빌드시 테스트 계정 생성


    //convert

    private Social createSocialProvider(User user, KakaoUserInfo userInfo) {
        return Social.builder()
                .provider(userInfo.getProvider())
                .clientId(userInfo.getId())
                .user(user)
                .build();
    }

    private User socialProviderSignToUser(KakaoUserInfo userInfo) {
        String email = userInfo.getEmail() != null ? userInfo.getEmail() : userInfo.getId()+kakaoAPI.getKAKAO_USER();
        String nickname = userInfo.getNickname() != null ? userInfo.getNickname() : userInfo.getId();
        String profileUrl = userInfo.getProfileImage();
        if(profileUrl == null) {
            profileUrl = filePathConfig.getUserDefaultProfilePath();
        }

        if(!verifyNickname(nickname)) {
            nickname =  Utils.getRenameNickname(userInfo.getNickname());
        }

        return User.builder()
                .email(email)
                .nickname(nickname)
                .profileUrl(profileUrl)
                .profileRenamedFilename(userInfo.getProfileImage())
                .build();
    }

    //로컬 유저 생성
    private User localSignUpRequestToUser(LocalSignUpRequest localSignUpRequest) {
        return User.builder()
                .email(localSignUpRequest.getEmail())
                .password(encoder.encode(localSignUpRequest.getPassword()))
                .nickname(localSignUpRequest.getNickname())
                .profileUrl(filePathConfig.getUserDefaultProfilePath())
                .profileRenamedFilename("user.png")
                .build();
    }


}
