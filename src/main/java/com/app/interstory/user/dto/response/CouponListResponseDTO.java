package com.app.interstory.user.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponListResponseDTO {
	private final List<CouponResponseDTO> coupons;
	private final Integer totalPage;

	public static CouponListResponseDTO from(List<CouponResponseDTO> coupons, Integer totalPage) {
		return CouponListResponseDTO.builder().coupons(coupons).totalPage(totalPage).build();
	}
}
