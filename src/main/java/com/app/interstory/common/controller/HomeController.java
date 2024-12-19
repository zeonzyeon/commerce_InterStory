package com.app.interstory.common.controller;

import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.service.RecommendationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.interstory.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

	private final RecommendationService recommendationService;

	@GetMapping()
	public String home(
		Model model,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		if (customUserDetails != null) {
			log.info("CurrentUser :::{}", customUserDetails.getUser());
			model.addAttribute("user", customUserDetails.getUser());
			log.info("user: {}", customUserDetails.getUser());
			List<NovelResponseDTO> recommendedNovels = recommendationService.getRecommendedNovels(customUserDetails);
			model.addAttribute("recommendedNovels", recommendedNovels);
		}
		return "index";
	}
}
