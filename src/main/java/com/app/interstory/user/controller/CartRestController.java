package com.app.interstory.user.controller;

import java.util.List;

import com.app.interstory.user.domain.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.user.dto.response.CartItemResponseDTO;
import com.app.interstory.user.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {

	private final CartService cartService;

	public CartRestController(CartService cartService) {
		this.cartService = cartService;
	}

	// 장바구니 아이템 조회
	@GetMapping
	public ResponseEntity<List<CartItemResponseDTO>> getCartItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<CartItemResponseDTO> cartItems = cartService.getCartItems(userDetails);
		return ResponseEntity.ok(cartItems);
	}

	// 장바구니 아이템 선택 삭제
	@DeleteMapping
	public ResponseEntity<String> deleteCartItems(
			@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody List<Long> cartItemIds) {
		if (cartItemIds == null || cartItemIds.isEmpty()) {
			return ResponseEntity.badRequest().body("Cart item IDs cannot be empty.");
		}
		cartService.deleteCartItems(userDetails, cartItemIds);
		return ResponseEntity.ok("Selected cart items deleted successfully.");
	}

	// 장바구니 아이템 선택 결제
	@PostMapping("/purchase-items")
	public ResponseEntity<String> purchaseCartItems(@RequestParam("userId") Long userId,
		@RequestBody List<Long> cartItemIds) {
		cartService.purchaseCartItems(userId, cartItemIds);
		return ResponseEntity.ok("Purchase successful.");
	}
}