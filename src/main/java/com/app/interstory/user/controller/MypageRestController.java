package com.app.interstory.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.user.domain.UserDetail;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.service.MypageService;
import com.app.interstory.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class MypageRestController {
	private final MypageService mypageService;

	@GetMapping
	public ResponseEntity<MypageResponseDTO> getUser(@AuthenticationPrincipal UserDetail userDetails) {
		User user = userDetails.getUser();

		MypageResponseDTO mypageResponseDTO = new MypageResponseDTO(
			user.getNickname(),
			user.getProfileUrl(),
			user.getPoint(),
			user.isSubscribe(),
			user.isAutoPayment()
		);

		return ResponseEntity.ok(mypageResponseDTO);
	}

	@PutMapping
	public ResponseEntity<UpdateUserResponseDTO> updateUser(@AuthenticationPrincipal UserDetail userDetails, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
		User user = userDetails.getUser();

		return ResponseEntity.ok(mypageService.updateUser(user, updateUserRequestDTO));
	}

	@GetMapping("/favorite-novels")
	public ResponseEntity<List<FavoriteNovelResponseDTO>> getFavoriteNovel(@AuthenticationPrincipal UserDetail userDetails) {
		return ResponseEntity.ok(mypageService.getFavoriteNovels(userDetails.getUser()));
	}
}
