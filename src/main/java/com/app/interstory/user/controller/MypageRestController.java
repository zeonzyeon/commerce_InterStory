package com.app.interstory.user.controller;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class MypageRestController {
	MypageService mypageService;

	@GetMapping
	public ResponseEntity<MypageResponseDTO> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = mypageService.findById(userDetails.getUser().getUserId());

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
	public ResponseEntity<UpdateUserResponseDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
		User user = mypageService.findById(userDetails.getUser().getUserId());

		return ResponseEntity.ok(mypageService.updateUser(user, updateUserRequestDTO));
	}
}
