package com.app.interstory.user.controller;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.dto.response.ReadNovelResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.service.MypageService;
import com.app.interstory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
	private final UserService userService;

	@GetMapping
	public ResponseEntity<MypageResponseDTO> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userService.findById(userDetails.getUser().getUserId());

		MypageResponseDTO mypageResponseDTO = new MypageResponseDTO(
			user.getNickname(),
			user.getProfileUrl(),
			user.getPoint(),
			user.getSubscribe(),
			user.getAutoPayment()
		);

		return ResponseEntity.ok(mypageResponseDTO);
	}

	@PutMapping
	public ResponseEntity<UpdateUserResponseDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
		User user = userService.findById(userDetails.getUser().getUserId());

		return ResponseEntity.ok(mypageService.updateUser(user, updateUserRequestDTO));
	}

	@GetMapping("/favorite-novels")
	public ResponseEntity<Page<FavoriteNovelResponseDTO>> getFavoriteNovel(@AuthenticationPrincipal CustomUserDetails userDetails, @PageableDefault(size = 10) Pageable pageable) {
		User user = userService.findById(userDetails.getUser().getUserId());
		return ResponseEntity.ok(mypageService.getFavoriteNovels(user, pageable));
	}

	@GetMapping("/read-novels")
	public ResponseEntity<Page<ReadNovelResponseDTO>> getReadNovel(@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		User user = userService.findById(userDetails.getUser().getUserId());
		return ResponseEntity.ok(mypageService.getReadNovels(user , pageable));
	}
}
