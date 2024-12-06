package com.app.interstory.user.service;

import com.app.interstory.config.FilePathConfig;
import com.app.interstory.config.globalExeption.customException.DuplicateEmailException;
import com.app.interstory.config.globalExeption.customException.DuplicateNicknameException;
import com.app.interstory.user.domain.entity.Social;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.vo.KakaoAPI;
import com.app.interstory.user.dto.vo.KakaoUserInfo;
import com.app.interstory.user.dto.request.LocalSignUpRequest;
import com.app.interstory.user.repository.SocialRepository;
import com.app.interstory.user.repository.UserRepository;
import com.app.interstory.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SocialRepository socialRepository;
    private final KakaoAPI kakaoAPI;
    private final FilePathConfig filePathConfig;


    public boolean verifyNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    public boolean verifyEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Transactional
    public User localSingUp(LocalSignUpRequest localSignUpRequest) {
        if (!verifyEmail(localSignUpRequest.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (!verifyNickname(localSignUpRequest.getNickname())) {
            throw new DuplicateNicknameException("이미 존재하는 닉네임입니다.");
        }

        return userRepository.save(localSignUpRequestToUser(localSignUpRequest));
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
                .build();
    }

    //로컬 유저 생성
    private User localSignUpRequestToUser(LocalSignUpRequest localSignUpRequest) {
        return User.builder()
                .email(localSignUpRequest.getEmail())
                .password(encoder.encode(localSignUpRequest.getPassword()))
                .nickname(localSignUpRequest.getNickname())
                .profileUrl(filePathConfig.getUserDefaultProfilePath())
                .build();
    }


}
