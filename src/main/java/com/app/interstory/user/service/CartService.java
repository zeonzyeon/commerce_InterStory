package com.app.interstory.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.response.CartItemResponseDTO;
import com.app.interstory.user.repository.CartItemRepository;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final EpisodeRepository episodeRepository;
	private final PointRepository pointRepository;

	// 장바구니 아이템 조회
	public List<CartItemResponseDTO> getCartItems(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		List<CartItem> cartItems = cartItemRepository.findByUser(user);

		return cartItems.stream()
			.map(item -> new CartItemResponseDTO(
				item.getCartItemId(),
				item.getEpisode().getEpisodeId(),
				item.getEpisode().getTitle()))
			.collect(Collectors.toList());
	}

	// 장바구니 아이템 삭제
	public void deleteCartItems(Long userId, List<Long> cartItemIds) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		List<CartItem> cartItems = cartItemRepository.findByUserAndCartItemIdIn(user, cartItemIds);

		if (cartItems.isEmpty()) {
			throw new RuntimeException("No matching cart items found for deletion.");
		}

		cartItemRepository.deleteAll(cartItems);
	}
}

