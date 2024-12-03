package com.app.interstory.user.service;

import com.app.interstory.user.domain.Roles;
import com.app.interstory.user.dto.request.LocalSignUpRequest;
import com.app.interstory.user.domain.User;
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

    public User localSingUp(LocalSignUpRequest localSignUpRequest) {
        return userRepository.save(localSignUpRequestToUser(localSignUpRequest));
    }



    //convert

    //로컬 유저 생성
    private User localSignUpRequestToUser(LocalSignUpRequest localSignUpRequest) {
        return User.builder()
                .email(localSignUpRequest.getEmail())
                .password(encoder.encode(localSignUpRequest.getPassword()))
                .nickname(localSignUpRequest.getNickname())
                .point(0L)
                .role(Roles.PUBLIC)
                .isActivity(true)
                .build();
    }
}
