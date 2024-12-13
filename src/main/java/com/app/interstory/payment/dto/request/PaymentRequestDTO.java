package com.app.interstory.payment.dto.request;

import com.app.interstory.payment.domain.enumtypes.PaymentType;

import lombok.Getter;

@Getter
public class PaymentRequestDTO {
	PaymentType paymentType;
	Long couponId;
}
