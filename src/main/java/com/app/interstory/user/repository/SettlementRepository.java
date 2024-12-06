package com.app.interstory.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.user.domain.entity.Settlement;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
	Optional<Settlement> findByUser_UserId(Long userId);
}
