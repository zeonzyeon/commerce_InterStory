package com.app.interstory.user.dto.request;

import lombok.Getter;

@Getter
public class LocalLoginRequest {
    private String email;
    private String password;
}
