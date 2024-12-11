package com.app.interstory.user.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LocalLoginRequest {
    private String email;
    private String password;
}
