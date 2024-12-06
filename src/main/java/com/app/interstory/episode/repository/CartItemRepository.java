package com.app.interstory.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.user.domain.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	boolean existsByUserAndEpisode(Long userId, Long episodeId);
}
