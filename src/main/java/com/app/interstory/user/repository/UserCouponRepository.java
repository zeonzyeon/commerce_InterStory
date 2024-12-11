package com.app.interstory.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.user.domain.entity.UserCoupon;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
	@EntityGraph(attributePaths = {"coupon"})
	Page<UserCoupon> findByUser_UserId(Long userId, Pageable pageable);
}
