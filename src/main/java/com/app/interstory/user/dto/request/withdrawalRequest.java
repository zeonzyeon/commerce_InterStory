package com.app.interstory.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class withdrawalRequest {
	private String reason;
	private String reasonDetail;
	private Long userId;
}
