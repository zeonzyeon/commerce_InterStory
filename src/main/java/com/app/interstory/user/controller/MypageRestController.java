package com.app.interstory.user.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.request.AccountRequestDTO;
import com.app.interstory.user.dto.request.SaveCouponRequestDTO;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.request.UserNicknameRequsetDto;
import com.app.interstory.user.dto.response.AccountResponseDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MyCommentResponseDTO;
import com.app.interstory.user.dto.response.MyNovelResponseDTO;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.dto.response.PointHistoryResponseDTO;
import com.app.interstory.user.dto.response.ReadNovelResponseDTO;
import com.app.interstory.user.dto.response.SettlementResponseDTO;
import com.app.interstory.user.dto.response.SubscriptionResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.dto.response.UserCouponResponseDTO;
import com.app.interstory.user.service.MypageService;
import com.app.interstory.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class MypageRestController {

	private final MypageService mypageService;
	private final UserService userService;

	// 이메일 중복 확인
	@GetMapping("/check-duplication-nickname")
	public ResponseEntity<?> checkDuplicationNickname(
		@RequestParam String nickname
	) {
		log.info("Check duplication nickname {}", nickname);
		Boolean checked = userService.verifyNickname(nickname);
		log.info("checked :  true 면 변경 가능함 ::: {}", checked);

		return ResponseEntity.ok(checked);
	}

	//회원 프로필 변경
	@PostMapping("/update-profile")
	public ResponseEntity<Map<String, Object>> updateProfile(
		@RequestParam("profileImage") MultipartFile file,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) throws IOException {

		if (file.getSize() > 10 * 1024 * 1024) {
			return ResponseEntity.badRequest().body(Map.of(
				"success", false,
				"message", "파일 크기는 10MB를 초과할 수 없습니다."
			));
		}

		String imageUrl = userService.updateProfileImage(
			currentUser.getUser().getUserId(),  // email
			file
		);
		log.info("imageUrl : {}", imageUrl);

		return ResponseEntity.ok().body(Map.of(
			"success", true,
			"message", "프로필 변경을 완료했습니다."
		));
	}

	@PostMapping("update-nickname")
	public ResponseEntity<Map<String, Object>> updateNickname(
		@RequestBody UserNicknameRequsetDto requset,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {

		userService.updateNickname(requset, currentUser);

		return ResponseEntity.ok().body(Map.of(
			"success", true,
			"message", "닉네임 변경 성공"
		));
	}

	@GetMapping
	public ResponseEntity<MypageResponseDTO> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(mypageService.getUser(userDetails));
	}

	@PutMapping
	public ResponseEntity<UpdateUserResponseDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
		return ResponseEntity.ok(mypageService.updateUser(userDetails, updateUserRequestDTO));
	}

	@GetMapping("/favorite-novels")
	public ResponseEntity<Page<FavoriteNovelResponseDTO>> getFavoriteNovel(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
		return ResponseEntity.ok(mypageService.getFavoriteNovels(userDetails, pageable));
	}

	@GetMapping("/read-novels")
	public ResponseEntity<Page<ReadNovelResponseDTO>> getReadNovel(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(mypageService.getReadNovels(userDetails, pageable));
	}

	@GetMapping("/point-history")
	public ResponseEntity<Page<PointHistoryResponseDTO>> getPointHistory(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10, sort = "usedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(mypageService.getPointHistory(userDetails, pageable));
	}

	@GetMapping("/my-novels")
	public ResponseEntity<Page<MyNovelResponseDTO>> getMyNovel(@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(mypageService.getMyNovels(userDetails, pageable));
	}

	@GetMapping("/settlement")
	public ResponseEntity<SettlementResponseDTO> getSettlement(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(mypageService.getSettlement(userDetails));
	}

	@PostMapping("/settlement")
	public ResponseEntity<Void> requestSettlement(@AuthenticationPrincipal CustomUserDetails userDetails) {
		mypageService.requestSettlement(userDetails);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/my-comments")
	public ResponseEntity<Page<MyCommentResponseDTO>> getMyComments(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(mypageService.getMyComments(userDetails, pageable));
	}

	@GetMapping("/account")
	public ResponseEntity<AccountResponseDTO> getAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(mypageService.getAccount(userDetails));
	}

	@PutMapping("/account")
	public ResponseEntity<AccountResponseDTO> updateAccount(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody AccountRequestDTO accountRequestDTO) {
		return ResponseEntity.ok(mypageService.updateAccount(userDetails, accountRequestDTO));
	}

	@GetMapping("/coupons")
	public ResponseEntity<Page<UserCouponResponseDTO>> getCoupons(
		@AuthenticationPrincipal CustomUserDetails userDetails, Pageable pageable) {
		return ResponseEntity.ok(mypageService.getCoupons(userDetails, pageable));
	}

	@PostMapping("/coupons")
	public ResponseEntity<UserCouponResponseDTO> saveCoupon(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SaveCouponRequestDTO saveCouponRequestDTO) {
		return ResponseEntity.ok(mypageService.saveCoupon(userDetails, saveCouponRequestDTO.getCouponCode()));
	}

	@GetMapping("/subscription")
	public ResponseEntity<SubscriptionResponseDTO> getSubscription(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(mypageService.getSubscription(userDetails));
	}
}
