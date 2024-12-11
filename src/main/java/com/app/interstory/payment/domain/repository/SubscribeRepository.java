package com.app.interstory.payment.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.payment.domain.entity.Subscribe;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
	Subscribe findByUser_UserId(Long userId);
}
