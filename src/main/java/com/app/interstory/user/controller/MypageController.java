package com.app.interstory.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.interstory.novel.service.NovelService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.response.PointHistoryResponseDTO;
import com.app.interstory.user.service.MypageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class MypageController {
	private final MypageService mypageService;
	private final NovelService novelService;

	// 회원 정보 수정

	// 포인트 내역 페이지
	@GetMapping("/point-history")
	public String getPointHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
		Pageable pageable, Model model) {
		Page<PointHistoryResponseDTO> pointHistoryPage = mypageService.getPointHistory(userDetails, pageable);
		model.addAttribute("pointHistoryPage", pointHistoryPage);
		return "mypage/point-history";
	}

	// 에피소드 리스트 페이지
	// @GetMapping("/novels/{id}/episodes")
	// public String showEpisodes(@PathVariable Long id, Model model) {
	// 	Novel novel = novelService.findById(id);
	// 	List<Episode> episodes = episodeService.findByNovelId(id);
	// 	List<Reaction> reactions = reactionService.analyzeByNovelId(id);
	//
	// 	model.addAttribute("novel", novel);
	// 	model.addAttribute("episodes", episodes);
	// 	model.addAttribute("reactions", reactions);
	// 	return "episodes/episode-list";
	// }

}
