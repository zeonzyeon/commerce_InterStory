package com.app.interstory.user.dto.response;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubscriptionResponseDTO {
	Timestamp endAt;
}
