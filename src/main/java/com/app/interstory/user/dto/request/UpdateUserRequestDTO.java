package com.app.interstory.user.dto.request;

import lombok.Getter;

@Getter
public class UpdateUserRequestDTO {
	private String profileUrl;
	private String nickname;
	private String password;
}
