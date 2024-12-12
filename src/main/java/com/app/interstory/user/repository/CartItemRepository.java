package com.app.interstory.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	boolean existsByUserAndEpisode(User user, Episode episode);

	List<CartItem> findByUser_UserId(Long userId);

	List<CartItem> findByUser_UserIdAndCartItemIdIn(Long userId, List<Long> cartItemIds);

	List<CartItem> findByCartItemIdInAndUser_UserId(List<Long> cartItemIds, Long userId);
}