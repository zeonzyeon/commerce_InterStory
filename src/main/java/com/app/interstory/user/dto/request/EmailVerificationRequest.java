package com.app.interstory.user.dto.request;

import lombok.Getter;

@Getter
public class EmailVerificationRequest {
    private String email;
    private String code;
}
