package com.app.interstory.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.ai.dto.ReactionAnalysisDto;
import com.app.interstory.ai.service.ReactionAnalysisService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AiController {

	private final ReactionAnalysisService reactionAnalysisService;

	// 이전 저장된 기록들 초기화
	// 서버 실행 시 한번만 실행
	// @DeleteMapping
	// public ResponseEntity<String> resetState() {
	// 	return ResponseEntity.ok(aiService.resetState());
	// }
	//
	// // 반환 될 메세지의 형식을 알려주는 프롬프트
	// // 서버 실행 시 한번만 실행
	// @PostMapping
	// public ResponseEntity<String> sendPrompt() {
	// 	String answer = aiService.sendPrompt();
	// 	return ResponseEntity.ok(answer);
	// }
	//
	// // 사용자의 정보를 넘겨주는 메소드
	// @PostMapping("/{userId}/novels/suggestion")
	// public ResponseEntity<String> getNovelSuggestion(@PathVariable Long userId) {
	// 	String answer = aiService.getNovelSuggestion(userId);
	// 	return ResponseEntity.ok(answer);
	// }

	@PostMapping("/ai/{episodeId}/reaction")
	public ResponseEntity<ReactionAnalysisDto> getReaction(@PathVariable Long episodeId) throws
		JsonProcessingException {
		ReactionAnalysisDto reactionAnalysisDto = reactionAnalysisService.getEpisodeReaction(episodeId);

		return ResponseEntity.ok(reactionAnalysisDto);
	}

}
