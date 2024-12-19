package com.app.interstory.novel.controller;

import com.app.interstory.novel.domain.enumtypes.SortType;
import com.app.interstory.novel.dto.response.CommentListResponseDto;
import com.app.interstory.novel.dto.response.EpisodeDetailResponseDto;
import com.app.interstory.novel.service.CommentService;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.enumtypes.Roles;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;
    private final CommentService commentService;

    @GetMapping("/detail/{episodeId}")
    public String episodeDetail(
            @PathVariable Long episodeId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request,
            Model model
    ) {

        EpisodeDetailResponseDto episodeDetail = episodeService.viewEpisodeDetail(episodeId, request);
        Boolean isLikeEpisode = episodeService.existEpisodeLike(episodeId, customUserDetails);
        model.addAttribute("episodeDetail", episodeDetail);
        model.addAttribute("isLikeEpisode", isLikeEpisode);
        return "novel/episode";
    }

    @GetMapping("/{episodeId}/comments")
    public String getEpisodeComments(
            Model model,
            @PathVariable("episodeId") Long episodeId,
            @RequestParam(defaultValue = "RECOMMENDATION") SortType sort,
            @RequestParam(defaultValue = "0") Integer page,
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        EpisodeDetailResponseDto episodeDetail = episodeService.viewEpisodeDetail(episodeId, request);
        CommentListResponseDto comments = commentService.getEpisodeComment(episodeId, sort, page, userDetails);

        Roles role = userDetails.getUser().getRole();
        Long userId = userDetails.getUser().getUserId();
        Boolean isLikeEpisode = episodeService.existEpisodeLike(episodeId, userDetails);

        model.addAttribute("role", role);
        model.addAttribute("userId", userId);
        model.addAttribute("episodeDetail", episodeDetail);
        model.addAttribute("comments", comments);
        model.addAttribute("currentPage", page);
        model.addAttribute("sort", sort);
        model.addAttribute("isLikeEpisode", isLikeEpisode);

        return "novel/comment";
    }

    @GetMapping("/{episodeId}/edit")
    public String editEpisodeForm(
            @PathVariable Long episodeId,
            HttpServletRequest request,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EpisodeDetailResponseDto episode = episodeService.viewEpisodeDetail(episodeId, request);
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("episode", episode);
        return "novel/episode-edit";
    }
}
