package com.app.interstory.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInactiveResponseDTO implements PaymentResponseDTO {
	private String cid;
	private String sid;
	private String status;
	private String created_at;
	private String inactivated_at;
	private String last_approved_at;
}