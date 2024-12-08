package com.app.interstory.novel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NovelService {
	private final NovelRepository novelRepository;
	private final UserRepository userRepository;
	private final EpisodeRepository episodeRepository;

	// 소설 작성
	@Transactional
	public Long writeNovel(NovelRequestDTO novelRequestDTO) {
		User user = userRepository.findById(novelRequestDTO.getUserId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		Novel novel = Novel.builder()
			.user(user)
			.title(novelRequestDTO.getTitle())
			.description(novelRequestDTO.getDescription())
			.plan(novelRequestDTO.getPlan())
			.status(NovelStatus.DRAFT)
			.thumbnailRenamedFilename(novelRequestDTO.getThumbnailRenamedFilename())
			.thumbnailUrl(novelRequestDTO.getThumbnailUrl())
			.tag(novelRequestDTO.getTag())
			.build();

		novelRepository.save(novel);
		return novel.getNovelId();
	}

	// 소설 수정
	public void updateNovel(Long novelId, NovelRequestDTO novelRequestDTO) {
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found"));

		novel.update(
			novelRequestDTO.getTitle(),
			novelRequestDTO.getDescription(),
			novelRequestDTO.getPlan(),
			novelRequestDTO.getThumbnailRenamedFilename(),
			novelRequestDTO.getThumbnailUrl(),
			novelRequestDTO.getTag(),
			novelRequestDTO.getStatus()
		);
	}

	// 소설 상세 조회
	@Transactional(readOnly = true)
	public NovelDetailResponseDTO readNovel(Long novelId, String sort) {
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found"));

		List<Episode> episodes;
		if ("recommendations".equals(sort)) {
			episodes = episodeRepository.findByNovelOrderByLikeCountDesc(novel);
		} else {
			episodes = episodeRepository.findByNovelOrderByPublishedAtDesc(novel);
		}

		List<EpisodeResponseDTO> episodeDTOs = episodes.stream()
			.map(episode -> EpisodeResponseDTO.builder()
				.episodeId(episode.getEpisodeId())
				.novelId(novelId)
				.title(episode.getTitle())
				.viewCount(episode.getViewCount())
				.publishedAt(episode.getPublishedAt())
				.thumbnailUrl(episode.getThumbnailUrl())
				.likeCount(episode.getLikeCount())
				.content(null)
				.status(episode.getStatus())
				.build()
			)
			.collect(Collectors.toList());

		return new NovelDetailResponseDTO(
			novel.getNovelId(),
			novel.getTitle(),
			novel.getDescription(),
			novel.getThumbnailUrl(),
			novel.getStatus(),
			novel.getTag(),
			episodeDTOs
		);
	}

	// 소설 목록 조회
	public Page<NovelResponseDTO> getNovelList(
		Long userId,
		String status,
		String title,
		String author,
		Boolean monetized,
		String tag,
		String sort,
		Pageable pageable
	) {
		Page<Novel> novels = novelRepository.findAllWithFiltersAndSort(
			userId, status, title, author, monetized, tag, sort, pageable
		);

		return novels.map(novel -> NovelResponseDTO.builder()
			.novelId(novel.getNovelId())
			.title(novel.getTitle())
			.description(novel.getDescription())
			.thumbnailUrl(novel.getThumbnailUrl())
			.likeCount(novel.getLikeCount())
			.status(novel.getStatus())
			.tag(novel.getTag())
			.build());
	}
}
