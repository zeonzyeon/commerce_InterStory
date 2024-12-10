package com.app.interstory.novel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
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
		@PathVariable("novelId") Long novelId,
		@RequestBody NovelRequestDTO novelRequestDTO
	) {
		novelService.updateNovel(novelId, novelRequestDTO);
		return ResponseEntity.ok("Novel updated successfully");
	}

	// 소설 상세 조회
	@GetMapping("/{novelId}")
	public ResponseEntity<NovelDetailResponseDTO> getNovelDetail(
		@PathVariable("novelId") Long novelId,
		@RequestParam(name = "sort", defaultValue = "latest") String sort) {
		NovelDetailResponseDTO response = novelService.readNovel(novelId, sort);
		return ResponseEntity.ok(response);
	}

	// 소설 목록 조회
	@GetMapping
	public ResponseEntity<Page<NovelResponseDTO>> getNovelList(
		@RequestParam(name = "userId", required = false) Long userId,
		@RequestParam(name = "status", required = false) NovelStatus status,
		@RequestParam(name = "title", required = false) String title,
		@RequestParam(name = "author", required = false) String author,
		@RequestParam(name = "monetized", required = false) Boolean monetized,
		@RequestParam(name = "tag", required = false) String tag,
		@RequestParam(name = "sort", defaultValue = "latest") String sort,
		Pageable pageable
	) {
		if (!"latest".equals(sort) && !"recommendations".equals(sort)) {
			throw new IllegalArgumentException("Invalid sort parameter: " + sort);
		}

		MainTag mainTag = null;
		if (tag != null) {
			try {
				mainTag = MainTag.valueOf(tag.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid tag parameter: " + tag);
			}
		}

		Page<NovelResponseDTO> novels = novelService.getNovelList(
			userId, status, title, author, monetized, mainTag, sort, null
		);
		return ResponseEntity.ok(novels);
	}

	// 소설 삭제
	@DeleteMapping("/{novelId}")
	public ResponseEntity<Void> deleteNovel(@PathVariable("novelId") Long id) {
		novelService.deleteNovel(id);
		return ResponseEntity.noContent().build();
	}
}

