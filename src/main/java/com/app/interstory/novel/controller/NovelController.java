package com.app.interstory.novel.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.interstory.novel.domain.enumtypes.Sort;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.service.CommentService;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.novel.service.NovelService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("novels")
@RequiredArgsConstructor
public class NovelController {
	private final NovelService novelService;
	private final UserService userService;
	private final EpisodeService episodeService;
	private final CommentService commentService;

	@GetMapping("/{novelId}")
	public String getNovel(Model model, @PathVariable("novelId") Long novelId,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "sort", defaultValue = "NEW_TO_OLD") Sort sort,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "showAll", defaultValue = "false") boolean showAll,
		@RequestParam(name = "commentSort", defaultValue = "RECOMMENDATION") Sort commentSort,
		@RequestParam(name = "commentPage", defaultValue = "0") Integer commentPage) {
		Long userId = userDetails.getUser().getUserId();
		NovelDetailResponseDTO novel = novelService.readNovel(novelId, userDetails);
		String nickname = userService.findById(novel.getAuthorId()).getNickname();

		int pageSize = 4;
		if (showAll)
			pageSize = 10000;

		Pageable pageable = PageRequest.of(page, pageSize);

		model.addAttribute("novel", novel);
		model.addAttribute("isAuthor", userId.equals(novel.getAuthorId()));
		model.addAttribute("author", nickname);
		model.addAttribute("episodes", episodeService.getEpisodeList(userDetails, novelId, sort, pageable, showAll));
		model.addAttribute("comments", commentService.getNovelComment(novelId, commentSort, commentPage, userDetails));

		return "novel/novel";
	}
}
