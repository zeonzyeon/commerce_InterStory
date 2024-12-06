package com.app.interstory.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	boolean existsByUserAndEpisode(User user, Episode episode);
}