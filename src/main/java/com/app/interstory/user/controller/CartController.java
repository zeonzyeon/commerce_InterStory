package com.app.interstory.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.service.MypageService;
import com.app.interstory.user.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final MypageService mypageService;
    private final CartService cartService;

    @GetMapping
    public String showCartPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        // 사용자 정보 가져오기
        MypageResponseDTO userInfo = mypageService.getUser(userDetails);
        model.addAttribute("user", userInfo);

        // 장바구니 아이템 가져오기
        var cartItems = cartService.getCartItems(userDetails);
        model.addAttribute("cartItems", cartItems);

        // 사용자 프로필 정보
        model.addAttribute("userNickname", userDetails.getUser().getNickname());
        model.addAttribute("userProfileImage", userDetails.getUser().getProfileUrl());

        return "cart/cart";  // templates/cart/cart.html을 렌더링
    }
}