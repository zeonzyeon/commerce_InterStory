package com.app.interstory.user.domain.enumtypes;

import lombok.Getter;

@Getter
public enum CouponEffect {

	POINT_1000_DISCOUNT(1000, 5000),
	POINT_3000_DISCOUNT(3000, 10000),
	POINT_5000_DISCOUNT(5000, 20000);

	private final int discountAmount;
	private final int minimumAmount;

	CouponEffect(int discountAmount, int minimumAmount) {
		this.discountAmount = discountAmount;
		this.minimumAmount = minimumAmount;
	}
}
