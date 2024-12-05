package com.app.interstory.user.controller;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.dto.response.PointHistoryResponseDTO;
import com.app.interstory.user.dto.response.ReadNovelResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class MypageRestController {
	private final MypageService mypageService;

	@GetMapping
	public ResponseEntity<MypageResponseDTO> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(mypageService.getUser(userDetails));
	}

	@PutMapping
	public ResponseEntity<UpdateUserResponseDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
		return ResponseEntity.ok(mypageService.updateUser(userDetails, updateUserRequestDTO));
	}

	@GetMapping("/favorite-novels")
	public ResponseEntity<Page<FavoriteNovelResponseDTO>> getFavoriteNovel(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
		return ResponseEntity.ok(mypageService.getFavoriteNovels(userDetails, pageable));
	}

	@GetMapping("/read-novels")
	public ResponseEntity<Page<ReadNovelResponseDTO>> getReadNovel(@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(mypageService.getReadNovels(userDetails, pageable));
	}

	@GetMapping("/point-history")
	public ResponseEntity<Page<PointHistoryResponseDTO>> getPointHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10, sort = "usedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(mypageService.getPointHistory(userDetails, pageable));
	}
}
