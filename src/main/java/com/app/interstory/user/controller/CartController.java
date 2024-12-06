package com.app.interstory.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.user.dto.response.CartItemResponseDTO;
import com.app.interstory.user.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	// 장바구니 아이템 조회
	@GetMapping
	public ResponseEntity<List<CartItemResponseDTO>> getCartItems(@RequestBody Map<String, Long> request) {
		Long userId = request.get("userId");
		List<CartItemResponseDTO> cartItems = cartService.getCartItems(userId);
		return ResponseEntity.ok(cartItems);
	}

}