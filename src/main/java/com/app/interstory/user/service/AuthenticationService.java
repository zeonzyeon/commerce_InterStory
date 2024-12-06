package com.app.interstory.user.service;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.vo.KAKAOAuthResponse;
import com.app.interstory.user.dto.vo.KakaoAPI;
import com.app.interstory.user.dto.vo.KakaoUserInfo;
import com.app.interstory.user.dto.request.LocalLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final KakaoAPI kakaoAPI;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    //커스텀 유저 생성 (세션에 저장) - local
    public CustomUserDetails authenticateLocal(LocalLoginRequest loginRequest) throws AuthenticationException {

        Authentication authentication = localAuthentication(loginRequest);

        log.debug("Authentication successful: {}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return (CustomUserDetails) authentication.getPrincipal();
    }

    //커스텀 유저 생성 (세션에 저장) - social
    public CustomUserDetails authenticateKakao(User socialLoginUser) {

        CustomUserDetails userDetails = customUserDetailsService.loadUserBySocial(socialLoginUser);
        Authentication authentication = sociallocalAuthentication(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return userDetails;
    }

    //소셜 로그인 loadByLoadUser
    private Authentication sociallocalAuthentication(CustomUserDetails userDetails) {

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,  // credentials를 null로 설정
                userDetails.getAuthorities()
        );
    }

    //로컬 로그인 loadByLoadUser
    private Authentication localAuthentication(LocalLoginRequest loginRequest){
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
    }



    //카카오 로그인
    public User kakaoLogin(String code) {

        String accessToken = getKakaoAccessToken(code);
        KakaoUserInfo userInfo = getKakaoUserInfo(accessToken);

        //기존 회원인지 확인 후 없을 시 유저 생성
        return userService.findByUserOrSave(userInfo);
    }

    //인증 코드로 액세스 토큰 요청
    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoAPI.getClientId());
        params.add("redirect_uri", kakaoAPI.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KAKAOAuthResponse> response = restTemplate.postForEntity(
                kakaoAPI.getKAKAO_TOKEN_URL(),
                request,
                KAKAOAuthResponse.class
        );

        if (response.getBody() != null) {
            return response.getBody().getAccessToken();
        }

        throw new IllegalArgumentException("kakao token 인증 실패");
    }

    //사용자 정보 요청
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        //setBearerAuth(accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        //setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                kakaoAPI.getKAKAO_USER_INFO_URL(),
                HttpMethod.GET,
                request,
                KakaoUserInfo.class
        );

        if (response.getBody() != null) {
            return response.getBody();
        }

        throw new IllegalArgumentException("카카오 사용자 정보를 가져오는데 실패했습니다.");
    }

}
