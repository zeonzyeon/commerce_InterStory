package com.app.interstory.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAmountDTO {
	Integer total;
	Integer tax_free;
	Integer vat;
	Integer point;
	Integer discount;
	Integer green_deposit;
}
