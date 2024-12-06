package com.app.interstory.user.dto.vo;

import com.app.interstory.user.domain.enumtypes.Provider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Getter
public class KakaoAPI {

    @Value("${kakao.client_id}")
    private String clientId;
    @Value("${kakao.secret_key}")
    private String secretKey;
    @Value("${kakao.redirect_uri}")
    private String redirectUri;
    @Value("${kakao.admin_key}")
    private String adminKey;

    private final Provider authProvider = Provider.KAKAO;
    private final String KAKAO_USER="@kakao.user";
    private final String KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private final String KAKAO_USER_WITHDRAWAL_URL = "https://kapi.kakao.com/v1/user/unlink";
    private final String KAKAO_KAUTH_URL = "https://kauth.kakao.com/oauth/authorize";

    public String getAuthorizationUrl() {
        return UriComponentsBuilder
                .fromUriString(KAKAO_KAUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build()
                .toString();
    }
}
