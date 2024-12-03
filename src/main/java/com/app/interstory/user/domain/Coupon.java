package com.app.interstory.user.domain;

import java.sql.Timestamp;

import jakarta.persistence.*;
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

	@Column(name = "minimum", nullable = false)
	private Long minimum;

	@Column(name = "discount", nullable = false)
	private Long discount;
}
