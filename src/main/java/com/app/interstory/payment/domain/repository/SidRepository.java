package com.app.interstory.payment.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.payment.domain.entity.Sid;

@Repository
public interface SidRepository extends JpaRepository<Sid, Long> {
	Sid findByUser_UserId(Long userId);

	Boolean existsByUser_UserId(Long userId);
}
