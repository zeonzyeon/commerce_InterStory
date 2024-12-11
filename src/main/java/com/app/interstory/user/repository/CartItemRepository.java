package com.app.interstory.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	boolean existsByUserAndEpisode(User user, Episode episode);

	List<CartItem> findByUser_UserId(Long userId);

	List<CartItem> findByUserAndCartItemIdIn(User user, List<Long> cartItemIds);

	List<CartItem> findByCartItemIdInAndUser(List<Long> cartItemIds, User user);
}