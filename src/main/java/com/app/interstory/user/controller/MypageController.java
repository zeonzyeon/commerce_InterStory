package com.app.interstory.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.interstory.novel.domain.enumtypes.SortType;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.service.NovelService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.response.AccountResponseDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MyCommentResponseDTO;
import com.app.interstory.user.dto.response.MyNovelResponseDTO;
import com.app.interstory.user.dto.response.PointHistoryResponseDTO;
import com.app.interstory.user.dto.response.ReadNovelResponseDTO;
import com.app.interstory.user.dto.response.SettlementResponseDTO;
import com.app.interstory.user.dto.response.UserCouponResponseDTO;
import com.app.interstory.user.dto.response.UserResponseDTO;
import com.app.interstory.user.service.MypageService;
import com.app.interstory.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("users")
@RequiredArgsConstructor
public class MypageController {

	private final MypageService mypageService;
	private final NovelService novelService;
	private final UserService userService;

	// 회원 정보 수정
	@GetMapping("/edit-profile")
	public String userEditProfile(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		Model model
	) {

		UserResponseDTO currentUser = userService.getCurrentUser(userDetails.getUser().getUserId());
		model.addAttribute("user", currentUser);

		return "mypage/edit-profile";
	}

	// 관심 작품 페이지
	@GetMapping("/favorite-novels")
	public String getFavoriteNovels(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		Page<FavoriteNovelResponseDTO> favoriteNovels = mypageService.getFavoriteNovels(userDetails,
			Pageable.unpaged());
		model.addAttribute("favoriteNovels", favoriteNovels);
		return "mypage/favorite-novel";
	}

	// 열람 작품 페이지
	@GetMapping("/recent-novels")
	public String getRecentNovels(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "sort", defaultValue = "NEW_TO_OLD") SortType sort,
		@PageableDefault(size = 10) Pageable pageable,
		Model model) {

		Pageable sortedPageable = PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			mapSort(sort)
		);

		Page<ReadNovelResponseDTO> recentNovels = mypageService.getReadNovels(userDetails, sortedPageable);
		model.addAttribute("recentNovels", recentNovels);
		model.addAttribute("currentSort", sort);

		return "mypage/recent-novel";
	}

	// 포인트 내역 페이지
	@GetMapping("/point-history")
	public String getPointHistory(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model) {

		Pageable pageable = PageRequest.of(page - 1, size);

		Page<PointHistoryResponseDTO> pointHistoryPage = mypageService.getPointHistory(userDetails, pageable);

		model.addAttribute("pointHistoryList", pointHistoryPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pointHistoryPage.getTotalPages());

		return "mypage/point-history";
	}

	// 보유 쿠폰 페이지
	@GetMapping("/my-coupons")
	public String getCouponPage(@AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable,
		Model model) {
		Page<UserCouponResponseDTO> coupons = mypageService.getCoupons(userDetails, pageable);
		model.addAttribute("coupons", coupons.getContent());
		return "mypage/my-coupons";
	}

	// 연재 작품 페이지
	@GetMapping("/my-novel")
	public String getMyNovels(@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10) Pageable pageable,
		Model model) {
		// 연재 작품 목록
		Page<MyNovelResponseDTO> myNovels = mypageService.getMyNovels(userDetails, pageable);
		model.addAttribute("myNovels", myNovels.getContent());

		// 계좌 정보
		AccountResponseDTO accountInfo = mypageService.getAccount(userDetails);
		model.addAttribute("accountInfo", accountInfo);

		// 정산 정보
		SettlementResponseDTO settlement = mypageService.getSettlement(userDetails);
		model.addAttribute("settlement", settlement);
		return "mypage/novel-list";
	}

	// 댓글 페이지
	@GetMapping("/my-comments")
	public String getMyComments(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "sort", defaultValue = "NEW_TO_OLD") SortType sort,
		@PageableDefault(size = 10) Pageable pageable,
		Model model) {

		Pageable sortedPageable = PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			mapSort(sort)
		);

		Page<MyCommentResponseDTO> myComments = mypageService.getMyComments(userDetails, sortedPageable);
		model.addAttribute("myComments", myComments);
		model.addAttribute("currentSort", sort);

		return "mypage/my-comments";
	}

	// // 회원정보 수정 페이지
	// @GetMapping("/edit-profile")
	// public String editProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
	// 	MypageResponseDTO userInfo = mypageService.getUser(userDetails);
	// 	model.addAttribute("userInfo", userInfo);
	// 	return "mypage/edit-profile";
	// }

	// // 회원정보 수정 처리
	// @PostMapping("/edit-profile")
	// public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
	// 	@ModelAttribute UpdateUserRequestDTO updateUserRequestDTO,
	// 	RedirectAttributes redirectAttributes) {
	// 	mypageService.updateUser(userDetails, updateUserRequestDTO);
	// 	redirectAttributes.addFlashAttribute("successMessage", "회원정보가 성공적으로 수정되었습니다.");
	// 	return "redirect:/users/edit-profile";
	// }

	// 회차 리스트와 반응 분석 페이지
	@GetMapping("/novels/{novelId}")
	public String getEpisodeListAndReaction(@PathVariable Long novelId,
		@RequestParam(name = "sort", defaultValue = "NEW_TO_OLD") SortType sort,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		Model model) {

		Sort sortOrder = mapSort(sort);
		Pageable pageable = PageRequest.of(page, size, sortOrder);
		NovelDetailResponseDTO novelDetail = novelService.readNovel(novelId, userDetails);

		model.addAttribute("novelDetail", novelDetail);
		model.addAttribute("currentSort", sort);
		return "mypage/episode-list";
	}

	// 정렬 변환 메서드
	private Sort mapSort(SortType sort) {
		switch (sort) {
			case OLD_TO_NEW:
				return Sort.by(Sort.Direction.ASC, "createdAt");
			case RECOMMENDATION:
				return Sort.by(Sort.Direction.DESC, "likeCount");
			case NEW_TO_OLD:
			default:
				return Sort.by(Sort.Direction.DESC, "createdAt");
		}
	}
}
