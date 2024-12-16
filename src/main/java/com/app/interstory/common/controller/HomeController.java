package com.app.interstory.common.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.interstory.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

	@GetMapping()
	public String home(
		Model model,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		if (customUserDetails != null) {
			log.info("CurrentUser :::{}", customUserDetails.getUser());
			model.addAttribute("user", customUserDetails.getUser());
			log.info("user: {}", customUserDetails.getUser());
		}
		return "index";
	}
}
