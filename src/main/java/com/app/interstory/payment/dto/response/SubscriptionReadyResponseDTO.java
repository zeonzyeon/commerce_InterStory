package com.app.interstory.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionReadyResponseDTO extends PaymentReadyResponseDTO {
	private String sid; // 정기 결제용 ID
}
