package com.app.interstory.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MypageResponseDTO {
	private String nickname;
	private String profileUrl;
	private Long point;
	private Boolean isSubscribed;
	private Boolean isAutoPayment;
}
