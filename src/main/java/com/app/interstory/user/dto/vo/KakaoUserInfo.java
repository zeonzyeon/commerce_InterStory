package com.app.interstory.user.dto.vo;

import com.app.interstory.user.domain.enumtypes.Provider;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class KakaoUserInfo {

    private String id;
    private Provider provider;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    public String getNickname() {
        return kakaoAccount.getProfile().getNickname();
    }

    public String getProfileImage() {
        return kakaoAccount.getProfile().getProfileImageUrl();
    }

    public void addAuthProvider(Provider provider) {
        this.provider = provider;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor
    public static class Profile {
        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}
