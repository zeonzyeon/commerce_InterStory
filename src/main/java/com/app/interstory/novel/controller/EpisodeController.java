package com.app.interstory.novel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.interstory.novel.dto.response.EpisodeDetailResponseDto;
import com.app.interstory.novel.service.EpisodeService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("episodes/")
@RequiredArgsConstructor
public class EpisodeController {

	private final EpisodeService episodeService;

	@PostMapping("detail/{episodeId}")
	public String episodeDetail(
		@PathVariable Long episodeId,
		HttpServletRequest request,
		Model model
	) {

		EpisodeDetailResponseDto episodeDetail = episodeService.viewEpisodeDetail(episodeId, request);

		model.addAttribute("episodeDetail", episodeDetail);

		return "mypage/episodeDetail";
	}

}
