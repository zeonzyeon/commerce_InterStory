package com.app.interstory.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.user.domain.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
