package com.app.interstory.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.config.globalExeption.customException.PaymentException;
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

	@PostMapping("/ready")
	public ResponseEntity<PaymentResponseDTO> readyPayment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PaymentRequestDTO paymentRequestDTO) {
		Long userId = userDetails.getUser().getUserId();

		if (paymentRequestDTO.getPaymentType() == PaymentType.NORMAL_FIRST) {
			return ResponseEntity.ok(kakaoService.kakaoPayReady(userId, PaymentType.NORMAL_FIRST));
		} else if (paymentRequestDTO.getPaymentType() == PaymentType.NORMAL_SECOND) {
			return ResponseEntity.ok(kakaoService.kakaoPayReady(userId, PaymentType.NORMAL_SECOND));
		} else if (paymentRequestDTO.getPaymentType() == PaymentType.NORMAL_THIRD) {
			return ResponseEntity.ok(kakaoService.kakaoPayReady(userId, PaymentType.NORMAL_THIRD));
		} else if (kakaoService.checkSid(userId)) {
			if (paymentRequestDTO.getPaymentType() == PaymentType.SEQUENCE)
				return ResponseEntity.ok(kakaoService.kakaoPayPayment(userId, PaymentType.SEQUENCE));
			else
				return ResponseEntity.ok(kakaoService.kakaoPayPayment(userId, PaymentType.AUTO));
		} else {
			if (paymentRequestDTO.getPaymentType() == PaymentType.SEQUENCE)
				return ResponseEntity.ok(kakaoService.kakaoPayReady(userId, PaymentType.SEQUENCE));
			else
				return ResponseEntity.ok(kakaoService.kakaoPayReady(userId, PaymentType.AUTO));
		}
	}

	@GetMapping("/success")
	public ResponseEntity<PaymentAproveResponseDTO> afterPayment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("pg_token") String pgToken) {
		return ResponseEntity.ok(kakaoService.kakaoPayApprove(userDetails.getUser().getUserId(), pgToken));
	}

	@GetMapping("/cancel")
	public void cancelPayment() {
		throw new PaymentException("결제가 취소되었습니다.");
	}

	@GetMapping("/fail")
	public void failPayment() {
		throw new PaymentException("결제에 실패하였습니다.");
	}

	@PostMapping("/inactive")
	public ResponseEntity<PaymentInactiveResponseDTO> inactivePayment(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(kakaoService.kakaoPayInactive(userDetails.getUser().getUserId()));
	}
}
