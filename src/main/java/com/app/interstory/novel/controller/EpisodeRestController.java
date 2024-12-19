package com.app.interstory.novel.controller;

import java.util.Map;

import com.app.interstory.novel.domain.enumtypes.EpisodeDirection;
import com.app.interstory.novel.dto.response.EpisodeAvailableResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.app.interstory.novel.dto.request.EpisodeRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/novels/episodes")
@RequiredArgsConstructor
public class EpisodeRestController {
	private final EpisodeService episodeService;

	// 회차 수정
	@PutMapping("/{episodeId}")
	public ResponseEntity<EpisodeResponseDTO> updateEpisode(
		@PathVariable Long episodeId,
		@RequestPart("episodeRequestDTO") EpisodeRequestDTO requestDTO,
		@RequestPart(value = "file", required = false) MultipartFile file,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		EpisodeResponseDTO responseDTO = episodeService.updateEpisode(episodeId, requestDTO, file, userDetails);
		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}

//	 // 회차 상세 조회
//	 @GetMapping("/{episodeId}")
//	 public ResponseEntity<EpisodeResponseDTO> readEpisode(
//	 	@PathVariable Long episodeId) {
//	 	EpisodeResponseDTO responseDTO = episodeService.readEpisode(episodeId);
//	 	return ResponseEntity.ok(responseDTO);
//	 }

	// 회차 삭제
	@DeleteMapping("/{episodeId}")
	public ResponseEntity<String> deleteEpisode(
		@PathVariable Long episodeId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
		episodeService.deleteEpisode(episodeId, userDetails);
		return ResponseEntity.noContent().build();
	}

	// 회차 구매
	@PostMapping("/{episodeId}/purchase")
	public ResponseEntity<String> purchaseEpisode(
		@PathVariable(name = "episodeId") Long episodeId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		episodeService.purchaseEpisode(userId, episodeId);
		return ResponseEntity.ok("Purchase successful!");
	}

	// 장바구니 담기
	@PostMapping("/{episodeId}/cart")
	public ResponseEntity<String> addToCart(
		@PathVariable(name = "episodeId") Long episodeId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		String result = episodeService.addItemToCart(userId, episodeId);
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

	// 이전 회차, 다음 회차 접근 검증
	@GetMapping("/{episodeId}/available")
	ResponseEntity<EpisodeAvailableResponseDTO> checkEpisodeAvailable(
			@PathVariable Long episodeId,
			@RequestParam EpisodeDirection direction,
			@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		EpisodeAvailableResponseDTO responseDTO =
				episodeService.checkEpisodeAvailable(episodeId, direction, userDetails);
		return ResponseEntity.ok(responseDTO);
	}

}
