package com.app.interstory.user.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSessionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String email;
    private String nickname;
    private String password;

}
