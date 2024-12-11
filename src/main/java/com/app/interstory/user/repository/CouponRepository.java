package com.app.interstory.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.user.domain.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
	Optional<Coupon> findByCode(String couponCode);
}
