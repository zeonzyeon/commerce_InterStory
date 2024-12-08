package com.app.interstory.novel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.service.NovelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/novels")
@RequiredArgsConstructor
public class NovelController {

	private final NovelService novelService;

	// 소설 작성
	@PostMapping
	public ResponseEntity<Long> writeNovel(@RequestBody NovelRequestDTO novelRequestDTO) {
		Long novelId = novelService.writeNovel(novelRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(novelId);
	}

	// 소설 수정
	@PutMapping("/{novelId}")
	public ResponseEntity<String> updateNovel(
		@PathVariable Long novelId,
		@RequestBody NovelRequestDTO novelRequestDTO
	) {
		novelService.updateNovel(novelId, novelRequestDTO);
		return ResponseEntity.ok("Novel updated successfully.");
	}
}

