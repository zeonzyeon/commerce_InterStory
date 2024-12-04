package com.app.interstory.user.service;

import com.app.interstory.config.globalExeption.customException.DuplicateEmailException;
import com.app.interstory.config.globalExeption.customException.DuplicateNicknameException;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.request.LocalSignUpRequest;
import com.app.interstory.user.repository.UserRepository;
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


    public boolean verifyNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Transactional
    public User localSingUp(LocalSignUpRequest localSignUpRequest) {
        if (userRepository.existsByEmail(localSignUpRequest.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(localSignUpRequest.getNickname())) {
            throw new DuplicateNicknameException("이미 존재하는 닉네임입니다.");
        }

        return userRepository.save(localSignUpRequestToUser(localSignUpRequest));
    }



    //convert

    //로컬 유저 생성
    private User localSignUpRequestToUser(LocalSignUpRequest localSignUpRequest) {
        return User.builder()
                .email(localSignUpRequest.getEmail())
                .password(encoder.encode(localSignUpRequest.getPassword()))
                .nickname(localSignUpRequest.getNickname())
//                .profileUrl("") 일단 s3 확인 후 추가
                .build();
    }

}
