package com.app.interstory.user.dto.request;

import java.sql.Timestamp;

import com.app.interstory.user.domain.enumtypes.CouponEffect;

import lombok.Getter;

@Getter
public class CouponRequestDTO {
	private Timestamp expiredAt;
	private String name;
	private CouponEffect effect;
}
