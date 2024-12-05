package com.app.interstory.user.controller;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.request.*;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.response.LoginResponse;
import com.app.interstory.user.service.AuthenticationService;
import com.app.interstory.user.service.EmailService;
import com.app.interstory.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final EmailService emailService;
    private final UserService userService;
    private final AuthenticationService authenticationService;


    //이메일 중복 확인 및 코드 전송
    @PostMapping("/email-verification-send")
    public ResponseEntity<Boolean> sendVerificationEmail(@RequestBody EmailRequest emailRequest) {
        String code = emailService.sendVerificationEmail(emailRequest.getEmail());
        log.info("code: {}", code);
        return ResponseEntity.ok().build();
    }

    //이메일 인증 확인
    @PostMapping("/email-verification")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody EmailVerificationRequest request)
    {
        boolean verification =emailService.verifyCode(request.getEmail(),request.getCode());
        log.info("verification: {}", verification);
        return ResponseEntity.ok(verification);
    }

    //닉네임 중복검사
    @PostMapping("/nickname-verification")
    public ResponseEntity<Boolean> verifyNickname (@RequestBody NicknameRequest nicknameRequest){
        boolean isAvailable = userService.verifyNickname(nicknameRequest.getNickname());
        return ResponseEntity.ok(isAvailable);
    }

    //회원 가입 폼 제출
     @PostMapping("/signup")
    public ResponseEntity<String> localSignup (@RequestBody LocalSignUpRequest localSignUpRequest){
        User savedUser = userService.localSingUp(localSignUpRequest);

        return ResponseEntity.ok("회원가입을 성공했습니다.");
     }

    //로컬 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> signup(@RequestBody LocalLoginRequest loginRequest , HttpSession session) {

        log.info("loginRequest: {}", loginRequest);
        CustomUserDetails userDetail = authenticationService.authenticateLocal(loginRequest);

        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok(
                LoginResponse.builder()
                        .redirectUrl("/")
                        .success(true)
                        .build()
        );
    }

}
