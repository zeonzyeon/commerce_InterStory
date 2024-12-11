package com.app.interstory.user.dto.response;

import static com.app.interstory.util.Utils.*;

import com.app.interstory.user.domain.entity.UserCoupon;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCouponResponseDTO {
	private String expiredAt;
	private String name;
	private String effect;

	public static UserCouponResponseDTO from(UserCoupon coupon) {
		return UserCouponResponseDTO.builder()
			.expiredAt(formatTimestamp(coupon.getCoupon().getExpiredAt()))
			.name(coupon.getCoupon().getName())
			.effect("포인트 " + coupon.getCoupon().getCouponEffect().getDiscountAmount() + "원 할인 (최소 " + coupon.getCoupon().getCouponEffect()
				.getMinimumAmount() + "원 이상 구매 시 적용)")
			.build();
	}
}
