package com.app.interstory.novel.controller;

import com.app.interstory.novel.domain.enumtypes.SortType;
import com.app.interstory.novel.dto.response.CommentListResponseDto;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.service.CommentService;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.user.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.interstory.novel.dto.response.EpisodeDetailResponseDto;
import com.app.interstory.novel.service.EpisodeService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("episodes/")
@RequestMapping("episodes")
@RequiredArgsConstructor
public class EpisodeController {

	private final EpisodeService episodeService;
    private final CommentService commentService;
    private final EpisodeService episodeService;

	@PostMapping("detail/{episodeId}")
	public String episodeDetail(
		@PathVariable Long episodeId,
		HttpServletRequest request,
		Model model
	) {
    @GetMapping("/{episodeId}/comments")
    public String getEpisodeComments(
            Model model,
            @PathVariable("episodeId") Long episodeId,
            @RequestParam(defaultValue = "RECOMMENDATION") SortType sort,
            @RequestParam(defaultValue = "0") Integer page,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

		EpisodeDetailResponseDto episodeDetail = episodeService.viewEpisodeDetail(episodeId, request);
        EpisodeResponseDTO episode = episodeService.readEpisode(episodeId);
        CommentListResponseDto comments = commentService.getEpisodeComment(episodeId, sort, page, userDetails);

		model.addAttribute("episodeDetail", episodeDetail);
        model.addAttribute("episode", episode);
        model.addAttribute("comments", comments);
        model.addAttribute("currentPage", page);
        model.addAttribute("sort", sort);

		return "mypage/episodeDetail";
	}

        return "novel/comment";
    }
}
