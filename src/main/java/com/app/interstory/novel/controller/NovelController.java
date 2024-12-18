package com.app.interstory.novel.controller;

import com.app.interstory.common.service.S3Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.interstory.novel.domain.enumtypes.SortType;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.service.CommentService;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.novel.service.NovelService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("novels")
@RequiredArgsConstructor
public class NovelController {
	private final NovelService novelService;
	private final UserService userService;
	private final EpisodeService episodeService;
	private final CommentService commentService;
	private final S3Service s3Service;

	@GetMapping("/{novelId}")
	public String getNovel(Model model, @PathVariable("novelId") Long novelId,
		MultipartFile file,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "sort", defaultValue = "NEW_TO_OLD") SortType sort,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "showAll", defaultValue = "false") boolean showAll,
		@RequestParam(name = "commentSort", defaultValue = "RECOMMENDATION") SortType commentSort,
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

	@GetMapping("/write")
	public String writeNovelForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		model.addAttribute("user", userDetails.getUser());
		return "novel/write";
	}
}
