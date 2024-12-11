package com.app.interstory.user.domain.entity;

import java.sql.Timestamp;

import com.app.interstory.user.domain.enumtypes.CouponEffect;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id")
	private Long couponId;

	@Column(name = "expired_at", nullable = false)
	private Timestamp expiredAt;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "code", nullable = false)
	private String code;

	@Enumerated(EnumType.STRING)
	@Column(name = "coupon_effect", nullable = false)
	private CouponEffect couponEffect;
}
