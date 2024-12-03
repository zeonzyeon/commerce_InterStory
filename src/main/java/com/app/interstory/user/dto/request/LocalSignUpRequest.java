package com.app.interstory.user.dto.request;

import lombok.Getter;

@Getter
public class LocalSignUpRequest {
    private String email;
    private String password;
    private String nickname;
}
