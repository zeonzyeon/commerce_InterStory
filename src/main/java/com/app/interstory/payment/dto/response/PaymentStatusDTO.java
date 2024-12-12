package com.app.interstory.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentStatusDTO {
	private Long partnerOrderId;
	private Long userCouponId;
}
