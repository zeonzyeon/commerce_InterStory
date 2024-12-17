package com.app.interstory.novel.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.Tag;
import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.domain.enumtypes.Sort;
import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.dto.request.NovelSortRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.dto.response.NovelListResponseDTO;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.novel.repository.TagRepository;
import com.app.interstory.user.domain.enumtypes.NovelSortType;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NovelService {
	private final NovelRepository novelRepository;
	private final UserRepository userRepository;
	private final EpisodeRepository episodeRepository;
	private final TagRepository tagRepository;
	@Qualifier("redisObjectTemplate")
	private final RedisTemplate<String, Object> redisTemplate;

	// 소설 작성
	public Long writeNovel(NovelRequestDTO novelRequestDTO, Long userId) {
		Novel novel = Novel.builder()
			.user(userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found")))
			.title(novelRequestDTO.getTitle())
			.description(novelRequestDTO.getDescription())
			.plan(novelRequestDTO.getPlan())
			.status(NovelStatus.DRAFT)
			.thumbnailRenamedFilename(novelRequestDTO.getThumbnailRenamedFilename())
			.thumbnailUrl(novelRequestDTO.getThumbnailUrl())
			.tag(novelRequestDTO.getTag())
			.isFree(novelRequestDTO.getIsFree())
			.build();

		novelRepository.save(novel);
		return novel.getNovelId();
	}

	// 소설 수정
	@Transactional
	public void updateNovel(Long novelId, NovelRequestDTO novelRequestDTO, Long userId) {
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found"));

		if (!novel.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized user");
		}

		novel.update(
			novelRequestDTO.getTitle(),
			novelRequestDTO.getDescription(),
			novelRequestDTO.getPlan(),
			novelRequestDTO.getThumbnailRenamedFilename(),
			novelRequestDTO.getThumbnailUrl(),
			novelRequestDTO.getTag(),
			novelRequestDTO.getStatus(),
			novelRequestDTO.getIsFree()
		);
	}

	// 소설 상세 조회
	public NovelDetailResponseDTO readNovel(Long novelId, Sort sort, Pageable pageable) {
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found"));

		Page<Episode> episodes;
		switch (sort) {
			case RECOMMENDATION:
				episodes = episodeRepository.findEpisodesByNovelIdOrderByLikeCount(novelId, pageable);
				break;
			case OLD_TO_NEW:
				episodes = episodeRepository.findEpisodesByNovelIdOrderByPublishedAtAsc(novelId, pageable);
				break;
			default:
				episodes = episodeRepository.findEpisodesByNovelIdOrderByPublishedAtDesc(novelId, pageable);
				break;
		}

		List<EpisodeResponseDTO> episodeDTOs = episodes.getContent().stream()
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
			novel.getPlan(),
			novel.getThumbnailUrl(),
			novel.getStatus(),
			novel.getTag(),
			episodeDTOs,
			episodes.getTotalPages()
		);
	}

	// 소설 목록 조회
	public NovelListResponseDTO getNovelList(
		NovelStatus status,
		String title,
		String author,
		Boolean monetized,
		MainTag tag,
		Sort sort,
		Integer page
	) {
		final int getItemCount = 10;

		Pageable pageable = PageRequest.of(page - 1, getItemCount);

		Page<Novel> novelPages = novelRepository.findByFilterAndSort(
			status, title, author, monetized, tag, sort.getDescription(), pageable
		);

		if (page > novelPages.getTotalPages()) {
			throw new RuntimeException("유효하지 않은 페이지입니다.");
		}

		List<NovelResponseDTO> novels = novelPages.stream()
			.map(novel -> {
				List<String> customTags = tagRepository.findByNovel(novel).stream()
					.map(Tag::getTag)
					.toList();
				return NovelResponseDTO.from(novel, customTags);
			})
			.toList();

		return NovelListResponseDTO.from(novels, novelPages.getTotalPages());
	}

	// 소설 삭제
	@Transactional
	public void deleteNovel(Long novelId, Long userId) {
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found with id: " + novelId));

		if (!novel.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("Unauthorized user");
		}

		if (novel.getStatus() == NovelStatus.DELETED) {
			throw new RuntimeException("Novel is already deleted.");
		}

		novel.markAsDeleted();
		novelRepository.save(novel);
	}

	// 태그별 인기 작품 캐싱 (TTL: 3시간)
	public List<NovelResponseDTO> getPopularNovelsByTag(NovelSortRequestDTO request) {

		String cacheKey = "popular:novels:" + request.getMainTag() + request.getType();
		List<NovelResponseDTO> cached = (List<NovelResponseDTO>)redisTemplate.opsForValue().get(cacheKey);

		if (cached == null) {
			log.info("**********cache refresh check*******************");
			cached = novelRepository.findPopularNovelsByTag(request).stream()
				.map(NovelResponseDTO::from)
				.toList();

			redisTemplate.opsForValue().set(cacheKey, cached, 3, TimeUnit.HOURS);
		}

		return cached;
	}

	//인기순 최신순 이름순 캐싱
	public List<NovelResponseDTO> getOrderedNovel(NovelSortType novelSortType) {

		String cacheKey = "popular:novels:cache:" + novelSortType;
		log.info("cacheKey*****{}", cacheKey);
		List<NovelResponseDTO> cached = (List<NovelResponseDTO>)redisTemplate.opsForValue().get(cacheKey);

		if (cached == null) {
			log.info("**********cache refresh check*******************");

			cached = novelRepository.findNovelByOrder(novelSortType).stream()
				.map(NovelResponseDTO::from)
				.toList();

			redisTemplate.opsForValue().set(cacheKey, cached, 3, TimeUnit.HOURS);
		}

		return cached;
	}

}
