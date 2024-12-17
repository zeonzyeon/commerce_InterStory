package com.app.interstory.novel.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.app.interstory.novel.domain.enumtypes.SortType;
import com.app.interstory.novel.dto.request.EpisodeRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeListResponseDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/novels/{novelId}/episodes")
@RequiredArgsConstructor
public class EpisodeRestController {

	private final EpisodeService episodeService;

	// 회차 작성
	@PostMapping
	public ResponseEntity<EpisodeResponseDTO> createEpisode(@PathVariable Long novelId,
		@RequestBody EpisodeRequestDTO requestDTO) {
		EpisodeResponseDTO responseDTO = episodeService.writeEpisode(novelId, requestDTO);
		return ResponseEntity.ok(responseDTO);
	}

	// 회차 수정
	@PutMapping("/{episodeId}")
	public ResponseEntity<EpisodeResponseDTO> updateEpisode(
		@PathVariable Long novelId,
		@PathVariable Long episodeId,
		@RequestBody EpisodeRequestDTO requestDTO) {
		EpisodeResponseDTO responseDTO = episodeService.updateEpisode(novelId, episodeId, requestDTO);
		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}

	// 회차 상세 조회
	@GetMapping("/{episodeId}")
	public ResponseEntity<EpisodeResponseDTO> readEpisode(
		@PathVariable Long novelId,
		@PathVariable Long episodeId) {
		EpisodeResponseDTO responseDTO = episodeService.readEpisode(novelId, episodeId);
		return ResponseEntity.ok(responseDTO);
	}

	// 회차 삭제
	@DeleteMapping("/{episodeId}")
	public ResponseEntity<String> deleteEpisode(
		@PathVariable Long novelId,
		@PathVariable Long episodeId) {
		episodeService.deleteEpisode(novelId, episodeId);
		return ResponseEntity.noContent().build();
	}

	// 회차 구매
	@PostMapping("/{episodeId}/purchase")
	public ResponseEntity<String> purchaseEpisode(
		@PathVariable Long novelId,
		@PathVariable Long episodeId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		episodeService.purchaseEpisode(userId, novelId, episodeId);
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

	// 회차 목록 조회
	@GetMapping("/list")
	public ResponseEntity<EpisodeListResponseDTO> getEpisodeList(
		@PathVariable(name = "novelId") Long novelId,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(name = "sort", defaultValue = "NEW_TO_OLD") SortType sort,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "showAll", defaultValue = "false") boolean showAll
	) {
		int pageSize = 4;
		if (showAll)
			pageSize = 10000;

		Pageable pageable = PageRequest.of(page, pageSize);

		EpisodeListResponseDTO responseDTO = episodeService.getEpisodeList(userDetails, novelId, sort, pageable, showAll);

		return ResponseEntity.ok(responseDTO);
	}

	//에피소드 목록 갖고오기
	@GetMapping
	public ResponseEntity<Page<EpisodeResponseDTO>> getEpisodes(
		@PathVariable Long novelId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "DESC") Sort.Direction direction
	) {

		Page<EpisodeResponseDTO> episodes = episodeService.getEpisodesByNovelId(novelId, page, direction);

		return ResponseEntity.ok(episodes);
	}

}
