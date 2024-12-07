package com.app.interstory.user.dto.response;

import static com.app.interstory.util.Utils.*;

import com.app.interstory.user.domain.entity.Coupon;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponResponseDTO {
	private Long couponId;
	private String expiredAt;
	private String name;
	private String code;
	private String effect;

	public static CouponResponseDTO from(Coupon coupon) {
		return CouponResponseDTO.builder()
			.couponId(coupon.getCouponId())
			.expiredAt(formatTimestamp(coupon.getExpiredAt()))
			.name(coupon.getName())
			.code(coupon.getCode())
			.effect("포인트 " + coupon.getCouponEffect().getDiscountAmount() + "원 할인 (최소 " + coupon.getCouponEffect()
				.getMinimumAmount() + "원 이상 구매 시 적용)")
			.build();
	}
}
