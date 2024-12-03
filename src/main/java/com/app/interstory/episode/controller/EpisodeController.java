package com.app.interstory.episode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.episode.domain.dto.EpisodeRequestDTO;
import com.app.interstory.episode.domain.dto.EpisodeResponseDTO;
import com.app.interstory.episode.service.EpisodeService;

@RestController
@RequestMapping("/api/novels/{novelId}/episodes")
public class EpisodeController {

	private final EpisodeService episodeService;

	public EpisodeController(EpisodeService episodeService) {
		this.episodeService = episodeService;
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
		return ResponseEntity.ok("Episode deleted successfully");
	}
}
