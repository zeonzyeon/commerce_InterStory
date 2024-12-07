package com.app.interstory.user.dto.response;

import com.app.interstory.user.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private Long UserId;
	private String email;
	private String nickname;
	private Long point;
	private String profileUrl;
	private Boolean isSubscribe;
	private Boolean isAutoPayment;

	public static UserResponse userToUserResponse(User user) {
		return UserResponse.builder()
			.UserId(user.getUserId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.point(user.getPoint())
			.profileUrl(user.getProfileUrl())
			.isSubscribe(user.getIsSubscribe())
			.isAutoPayment(user.getIsAutoPayment())
			.build();
	}
}
