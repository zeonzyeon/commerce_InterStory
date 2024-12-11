package com.app.interstory.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoController {
	@GetMapping("/kakaopay")
	public String kakaoPay() {
		return "payment/testPayment";
	}
}
