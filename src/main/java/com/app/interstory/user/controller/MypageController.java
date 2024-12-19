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
import com.app.interstory.novel.service.CommentService;
import com.app.interstory.novel.service.NovelService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MyCommentResponseDTO;
import com.app.interstory.user.dto.response.PointHistoryResponseDTO;
import com.app.interstory.user.dto.response.ReadNovelResponseDTO;
import com.app.interstory.user.dto.response.UserCouponResponseDTO;
import com.app.interstory.user.dto.response.UserResponseDTO;
import com.app.interstory.user.service.MypageService;
import com.app.interstory.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class MypageController {

	private final MypageService mypageService;
	private final NovelService novelService;
	private final UserService userService;
	private final CommentService commentService;

	//내 댓글 모음
	@GetMapping("/my-comment")
	public String showMyComments(
		Model model,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(defaultValue = "0", name = "page") int page
	) {

		Page<MyCommentResponseDTO> myComments =
			commentService.getMyComments(userDetails.getUser().getUserId(), page);

		model.addAttribute("myComments", myComments);
		model.addAttribute("user", userService.findById(userDetails.getUser().getUserId()));
		model.addAttribute("currentMenu", "comments");

		return "mypage/my-comment";
	}

	// 회원 정보 수정
	@GetMapping("/edit-profile")
	public String showUserEditProfile(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		Model model
	) {

		UserResponseDTO currentUser = userService.getCurrentUser(userDetails.getUser().getUserId());
		model.addAttribute("user", currentUser);
		model.addAttribute("currentMenu", "edit");

		return "mypage/edit-profile";
	}

	// 회원 정보 페이지
	@GetMapping("/profile")
	public String getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		Pageable pageable = PageRequest.of(0, 5);
		Pageable pageable2 = PageRequest.of(0, 100000);

		model.addAttribute("user", mypageService.getUser(userDetails));
		model.addAttribute("endAt", mypageService.getSubscription(userDetails).getEndAt());
		model.addAttribute("isSubscriptionContinue", mypageService.getSubscription(userDetails).getIsContinue());
		model.addAttribute("novels", mypageService.getMyNovels(userDetails, pageable));
		model.addAttribute("comments", mypageService.getMyComments(userDetails, pageable));
		model.addAttribute("myCoupons", mypageService.getCoupons(userDetails, pageable2));
		model.addAttribute("currentMenu", "profile");
		return "mypage/my-profile";
	}

	// 관심 작품 페이지
	@GetMapping("/favorite-novels")
	public String getFavoriteNovels(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		Page<FavoriteNovelResponseDTO> favoriteNovels = mypageService.getFavoriteNovels(userDetails,
			Pageable.unpaged());
		model.addAttribute("favoriteNovels", favoriteNovels);
		model.addAttribute("currentMenu", "favorites");
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
		model.addAttribute("currentMenu", "recent");

		return "mypage/recent-novel";
	}

	// 포인트 내역 페이지
	@GetMapping("/point-history")
	public String getPointHistory(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(defaultValue = "0", name = "page") int page,
		@RequestParam(defaultValue = "10", name = "size") int size,
		Model model) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "usedAt"));

		Page<PointHistoryResponseDTO> pointHistoryPage = mypageService.getPointHistory(userDetails, pageable);

		model.addAttribute("user", mypageService.getUser(userDetails));
		model.addAttribute("pointHistoryList", pointHistoryPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pointHistoryPage.getTotalPages());
		model.addAttribute("currentMenu", "points");

		return "mypage/point-history";
	}

	// 보유 쿠폰 페이지
	@GetMapping("/my-coupons")
	public String getCouponPage(@AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable,
		Model model) {
		Page<UserCouponResponseDTO> coupons = mypageService.getCoupons(userDetails, pageable);

		model.addAttribute("user", mypageService.getUser(userDetails));
		model.addAttribute("coupons", coupons);
		model.addAttribute("currentPage", pageable.getPageNumber());
		model.addAttribute("totalPages", coupons.getTotalPages());
		model.addAttribute("currentMenu", "coupons");
		return "mypage/my-coupons";
	}

	// 연재 작품 페이지
	@GetMapping("/my-novel")
	public String getMyNovels(@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10000) Pageable pageable, Model model) {
		model.addAttribute("user", mypageService.getUser(userDetails));
		model.addAttribute("myNovels", mypageService.getMyNovels(userDetails, pageable));
		model.addAttribute("accountInfo", mypageService.getAccount(userDetails));
		model.addAttribute("settlement", mypageService.getSettlement(userDetails).getFee());
		model.addAttribute("currentMenu", "myNovel");
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
