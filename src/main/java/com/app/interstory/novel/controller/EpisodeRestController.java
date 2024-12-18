package com.app.interstory.novel.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.novel.dto.request.EpisodeRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/novels/episodes")
@RequiredArgsConstructor
public class EpisodeRestController {
	private final EpisodeService episodeService;

	// 회차 수정
	@PutMapping("/{episodeId}")
	public ResponseEntity<EpisodeResponseDTO> updateEpisode(
		@PathVariable Long episodeId,
		@RequestBody EpisodeRequestDTO requestDTO) {
		EpisodeResponseDTO responseDTO = episodeService.updateEpisode(episodeId, requestDTO);
		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}

	// // 회차 상세 조회
	// @GetMapping("/{episodeId}")
	// public ResponseEntity<EpisodeResponseDTO> readEpisode(
	// 	@PathVariable Long episodeId) {
	// 	EpisodeResponseDTO responseDTO = episodeService.readEpisode(episodeId);
	// 	return ResponseEntity.ok(responseDTO);
	// }

	// 회차 삭제
	@DeleteMapping("/{episodeId}")
	public ResponseEntity<String> deleteEpisode(
		@PathVariable Long episodeId) {
		episodeService.deleteEpisode(episodeId);
		return ResponseEntity.noContent().build();
	}

	// 회차 구매
	@PostMapping("/{episodeId}/purchase")
	public ResponseEntity<String> purchaseEpisode(
		@PathVariable Long episodeId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		episodeService.purchaseEpisode(userId, episodeId);
		return ResponseEntity.ok("Purchase successful!");
	}

	// 장바구니 담기
	@PostMapping("/cart")
	public ResponseEntity<String> addToCart(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody Map<String, Long> request
	) {
		Long userId = userDetails.getUser().getUserId();
		String result = episodeService.addItemToCart(userId, request.get("episodeId"));
		return ResponseEntity.ok(result);
	}

	// 회차 추천
	@PostMapping("/{episodeId}/like")
	public ResponseEntity<String> likeEpisode(
		@PathVariable Long episodeId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		String message = episodeService.likeEpisode(userId, episodeId);
		return ResponseEntity.ok(message);
	}

}
