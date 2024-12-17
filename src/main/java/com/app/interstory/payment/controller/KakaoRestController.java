package com.app.interstory.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.payment.domain.enumtypes.PaymentType;
import com.app.interstory.payment.dto.request.PaymentRequestDTO;
import com.app.interstory.payment.dto.response.PaymentAproveResponseDTO;
import com.app.interstory.payment.dto.response.PaymentInactiveResponseDTO;
import com.app.interstory.payment.dto.response.PaymentResponseDTO;
import com.app.interstory.payment.service.KakaoService;
import com.app.interstory.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class KakaoRestController {
	private final KakaoService kakaoService;

	@PostMapping("/establish")
	public ResponseEntity<PaymentResponseDTO> establishPayment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PaymentRequestDTO paymentRequestDTO) {
		Long userId = userDetails.getUser().getUserId();
		Long couponId = paymentRequestDTO.getCouponId();

		PaymentType paymentType = paymentRequestDTO.getPaymentType();
		switch (paymentType) {
			case NORMAL_FIRST:
			case NORMAL_SECOND:
			case NORMAL_THIRD:
			case NORMAL_FOURTH:
			case NORMAL_FIFTH:
				return ResponseEntity.ok(kakaoService.kakaoPayReady(userId, paymentRequestDTO.getPaymentType(), couponId));
			case CHANGE_PAYMENT:
				return ResponseEntity.ok(kakaoService.kakaoPayChangePayment(userId));
			default:
				if (kakaoService.checkSid(userId)) {
					return ResponseEntity.ok(kakaoService.kakaoPayPayment(userId, paymentType));
				} else {
					return ResponseEntity.ok(kakaoService.kakaoPayReady(userId, paymentType, couponId));
				}
		}
	}

	@GetMapping("/request")
	public ResponseEntity<PaymentAproveResponseDTO> requestPayment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("pg_token") String pgToken) {
		return ResponseEntity.ok(kakaoService.kakaoPayApprove(userDetails.getUser().getUserId(), pgToken));
	}

	@GetMapping("/cancel")
	public ResponseEntity<String> cancelPayment() {
		return ResponseEntity.ok(kakaoService.kakaoPayCancel());
	}

	@GetMapping("/fail")
	public ResponseEntity<String> failPayment() {
		return ResponseEntity.ok(kakaoService.kakaoPayFail());
	}

	@PostMapping("/inactive-subscription")
	public ResponseEntity<PaymentInactiveResponseDTO> inactiveSubscription(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(kakaoService.kakaoPayInactiveSubscription(userDetails.getUser().getUserId()));
	}

	@PostMapping("/inactive-quto-payment")
	public ResponseEntity<PaymentInactiveResponseDTO> inactiveAutoPayment(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(kakaoService.kakaoPayInactiveAutoPayment(userDetails.getUser().getUserId()));
	}
}
