package com.app.interstory.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateUserResponseDTO {
	private String profileUrl;
	private String nickname;
	private String password;
}
