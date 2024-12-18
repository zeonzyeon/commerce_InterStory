package com.app.interstory.novel.controller;

import java.util.List;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.dto.request.EpisodeRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeListResponseDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.dto.response.NovelListResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.app.interstory.novel.service.RecommendationService;
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
import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.dto.request.NovelSortRequestDTO;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.novel.service.NovelService;
import com.app.interstory.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/novels")
@RequiredArgsConstructor
@Slf4j
public class NovelRestController {

	private final NovelService novelService;
	private final EpisodeService episodeService;
	private final RecommendationService recommendationService;

	// 소설 작성
	@PostMapping
	public ResponseEntity<Long> writeNovel(
		@RequestBody NovelRequestDTO novelRequestDTO,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		Long novelId = novelService.writeNovel(novelRequestDTO, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(novelId);
	}

	// 소설 수정
	@PutMapping("/{novelId}")
	public ResponseEntity<String> updateNovel(
		@PathVariable("novelId") Long novelId,
		@RequestBody NovelRequestDTO novelRequestDTO,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		novelService.updateNovel(novelId, novelRequestDTO, userId);
		return ResponseEntity.ok("Novel updated successfully");
	}

	// 소설 상세 조회
	@GetMapping("/{novelId}")
	public ResponseEntity<NovelDetailResponseDTO> readNovel(
		@PathVariable("novelId") Long novelId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		NovelDetailResponseDTO response = novelService.readNovel(novelId, userDetails);
		return ResponseEntity.ok(response);
	}

	// 소설 목록 조회
	@GetMapping
	public ResponseEntity<NovelListResponseDTO> getNovelList(
		@RequestParam(name = "status", required = false) NovelStatus status,
		@RequestParam(name = "title", required = false) String title,
		@RequestParam(name = "author", required = false) String author,
		@RequestParam(name = "monetized", required = false) Boolean monetized,
		@RequestParam(name = "tag", required = false) MainTag tag,
		@RequestParam(name = "sort", defaultValue = "NEW_TO_OLD") SortType sort,
		@RequestParam(defaultValue = "1") Integer page
	) {
		NovelListResponseDTO novels = novelService.getNovelList(status, title, author, monetized, tag, sort, page);
		return ResponseEntity.ok(novels);
	}

	// 소설 삭제
	@DeleteMapping("/{novelId}")
	public ResponseEntity<Void> deleteNovel(
		@PathVariable("novelId") Long novelId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long userId = userDetails.getUser().getUserId();
		novelService.deleteNovel(novelId, userId);
		return ResponseEntity.noContent().build();
	}

	// 관심작품 등록
	@PostMapping("/{novelId}/favorite")
	public ResponseEntity<String> likeEpisode(@PathVariable("novelId") Long novelId, @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getUserId();

		String message = novelService.likeNovel(userId, novelId);

		return ResponseEntity.ok(message);
	}

	//소설 조회 - 인기순 최신순 이름순
	@PostMapping("/sortType")
	public ResponseEntity<List<NovelResponseDTO>> getOrderedNovelList(
		@RequestBody NovelSortRequestDTO request
	) {
		List<NovelResponseDTO> novels = novelService.getOrderedNovel(request.getType());

		return ResponseEntity.ok(novels);
	}

	//소설 태그별 목록
	@PostMapping("/tag")
	public ResponseEntity<List<NovelResponseDTO>> getTagOrderedNovelList(
		@RequestBody NovelSortRequestDTO request
	) {
		log.info("getTagOrderedNovelList getMainTag ***: {}", request.getMainTag());
		List<NovelResponseDTO> novels = novelService.getPopularNovelsByTag(request);

		return ResponseEntity.ok(novels);
	}

	// 추천 소설 조회
	@GetMapping("/recommended-novel")
	public ResponseEntity<List<NovelResponseDTO>> getRecommendedNovelList(@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<NovelResponseDTO> novels = recommendationService.getRecommendedNovels(userDetails);
		return ResponseEntity.ok(novels);
	}

	// 회차 작성
	@PostMapping("/{novelId}/episodes")
	public ResponseEntity<EpisodeResponseDTO> createEpisode(@PathVariable Long novelId,
		@RequestBody EpisodeRequestDTO requestDTO) {
		EpisodeResponseDTO responseDTO = episodeService.writeEpisode(novelId, requestDTO);
		return ResponseEntity.ok(responseDTO);
	}

	// 회차 목록 조회
	@GetMapping("/{novelId}/episodes/list")
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
	@GetMapping("/{novelId}/episodes")
	public ResponseEntity<Page<EpisodeResponseDTO>> getEpisodes(
		@PathVariable Long novelId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "DESC") Sort.Direction direction
	) {

		Page<EpisodeResponseDTO> episodes = episodeService.getEpisodesByNovelId(novelId, page, direction);

		return ResponseEntity.ok(episodes);
	}
}
