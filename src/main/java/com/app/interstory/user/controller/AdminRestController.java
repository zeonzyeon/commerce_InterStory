package com.app.interstory.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.request.CouponRequestDTO;
import com.app.interstory.user.dto.request.NoticeRequestDTO;
import com.app.interstory.user.dto.response.CouponListResponseDTO;
import com.app.interstory.user.dto.response.CouponResponseDTO;
import com.app.interstory.user.dto.response.NoticeListResponseDTO;
import com.app.interstory.user.dto.response.NoticeResponseDTO;
import com.app.interstory.user.dto.response.UserListResponseDTO;
import com.app.interstory.user.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {
	private final AdminService adminService;

	@GetMapping("/users")
	public ResponseEntity<UserListResponseDTO> getUsers(@RequestParam(defaultValue = "1") Integer page,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(adminService.getUsers(page, userDetails));
	}

	@GetMapping("/users/search")
	public ResponseEntity<UserListResponseDTO> searchUsers(
		@RequestParam(required = false) String nickname,
		@RequestParam(required = false) String email,
		@RequestParam(defaultValue = "1") Integer page,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(adminService.searchUsers(nickname, email, page, userDetails));
	}

	@PostMapping("/users/{userId}/active")
	public ResponseEntity<String> activeUser(@PathVariable Long userId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(adminService.activeUser(userId, userDetails));
	}

	@PostMapping("/notices")
	public ResponseEntity<String> writeNotice(@RequestBody NoticeRequestDTO noticeRequestDTO,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(adminService.writeNotice(noticeRequestDTO, userDetails));
	}

	@PutMapping("/notices/{noticeId}")
	public ResponseEntity<String> updateNotice(@PathVariable Long noticeId,
		@RequestBody NoticeRequestDTO noticeRequestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(adminService.updateNotice(noticeId, noticeRequestDTO, userDetails));
	}

	@GetMapping("/notices")
	public ResponseEntity<NoticeListResponseDTO> getNoticeList(@RequestParam(defaultValue = "1") Integer page) {
		return ResponseEntity.ok(adminService.getNoticeList(page));
	}

	@GetMapping("/notices/{noticeId}")
	public ResponseEntity<NoticeResponseDTO> getNoticeDetail(@PathVariable Long noticeId) {
		return ResponseEntity.ok(adminService.getNoticeDetail(noticeId));
	}

	@DeleteMapping("/notices/{noticeId}")
	public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		adminService.deleteNotice(noticeId, userDetails);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/coupons")
	public ResponseEntity<CouponResponseDTO> generateCoupon(@RequestBody CouponRequestDTO couponRequestDTO,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(adminService.generateCoupon(couponRequestDTO, userDetails));
	}

	@GetMapping("/coupons")
	public ResponseEntity<CouponListResponseDTO> getCoupons(@RequestParam(defaultValue = "1") Integer page,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(adminService.getCoupons(page, userDetails));
	}

	@DeleteMapping("/coupons/{couponId}")
	public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		adminService.deleteCoupon(couponId, userDetails);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/plan/{novelId}/approval")
	public ResponseEntity<Void> handlePlanApproval(@PathVariable Long novelId, @RequestParam Boolean approve, @AuthenticationPrincipal CustomUserDetails userDetails) {
		adminService.handlePlanApproval(novelId, approve, userDetails);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/novel/{novelId}/restore")
	public ResponseEntity<Void> restoreDeletedNovel(@PathVariable Long novelId, @AuthenticationPrincipal CustomUserDetails userDetails) {
		adminService.restoreDeletedNovel(novelId, userDetails);
		return ResponseEntity.ok().build();
	}
}
