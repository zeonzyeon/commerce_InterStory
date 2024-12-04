package com.app.interstory.user.service;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.request.LocalLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;

    public CustomUserDetails authenticateLocal(LocalLoginRequest loginRequest) throws AuthenticationException {

        log.info("***요청 비밀번호:{}", loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        log.debug("Authentication successful: {}", authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication successful: {}", authentication.getPrincipal());
        return (CustomUserDetails) authentication.getPrincipal();
    }

}
