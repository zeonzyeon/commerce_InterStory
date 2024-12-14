package com.app.interstory.novel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.EpisodeLike;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.dto.request.EpisodeRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.dto.response.MayPageNovelResponseDto;
import com.app.interstory.novel.repository.CartItemRepository;
import com.app.interstory.novel.repository.EpisodeLikeRepository;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.NovelRepositoryQueryDsl;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EpisodeService {
	private final NovelRepositoryQueryDsl novelRepository;
	private final EpisodeRepository episodeRepository;
	private final UserRepository userRepository;
	private final PointRepository pointRepository;
	private final CartItemRepository cartItemRepository;
	private final EpisodeLikeRepository episodeLikeRepository;

	// 회차 작성
	@Transactional
	public EpisodeResponseDTO writeEpisode(Long novelId, EpisodeRequestDTO requestDTO) {
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found"));

		if (requestDTO.getTitle() == null || requestDTO.getTitle().isEmpty()) {
			throw new IllegalArgumentException("Episode title cannot be null");
		}

		if (requestDTO.getContent() == null || requestDTO.getContent().isEmpty()) {
			throw new IllegalArgumentException("Episode content cannot be null");
		}

		Episode episode = Episode.builder()
			.novel(novel)
			.title(requestDTO.getTitle())
			.thumbnailRenamedFilename(requestDTO.getThumbnailRenamedFilename())
			.thumbnailUrl(requestDTO.getThumbnailUrl())
			.content(requestDTO.getContent())
			.status(true)
			.build();

		return convertToDTO(episode);
	}

	// 회차 수정
	@Transactional
	public EpisodeResponseDTO updateEpisode(Long novelId, Long episodeId, EpisodeRequestDTO requestDTO) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovel().getNovelId().equals(novelId)) {
			throw new IllegalArgumentException("Invalid novelId for the given episode.");
		}

		episode.updateEpisode(requestDTO);

		return convertToDTO(episode);
	}

	// 회차 상세 조회
	public EpisodeResponseDTO readEpisode(Long novelId, Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovel().getNovelId().equals(novelId)) {
			throw new RuntimeException("Episode does not belong to the specified novel");
		}

		return convertToDTO(episode);
	}

	private EpisodeResponseDTO convertToDTO(Episode episode) {
		return EpisodeResponseDTO.builder()
			.episodeId(episode.getEpisodeId())
			.novelId(episode.getNovel().getNovelId())
			.title(episode.getTitle())
			.viewCount(episode.getViewCount())
			.publishedAt(episode.getPublishedAt())
			.thumbnailUrl(episode.getThumbnailUrl())
			.likeCount(episode.getLikeCount())
			.content(episode.getContent())
			.status(episode.getStatus())
			.build();
	}

	// 회차 삭제
	@Transactional
	public void deleteEpisode(Long novelId, Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovel().getNovelId().equals(novelId)) {
			throw new RuntimeException("Episode does not belong to the specified novel");
		}

		episode.markAsDeleted();
	}

	// 회차 구매
	@Transactional
	public void purchaseEpisode(Long userId, Long novelId, Long episodeId) {
		// 1. 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// 2. 에피소드 조회 및 검증
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovel().getNovelId().equals(novelId)) {
			throw new RuntimeException("Invalid novel ID for the given episode.");
		}

		// 3. 에피소드 가격 정의
		Long episodePrice = 500L; // 임시 포인트

		// 4. 포인트 차감
		user.reducePointsForPurchase(episodePrice);

		// 5. 포인트 사용 내역 저장
		Point point = Point.builder()
			.user(user)
			.balance(-episodePrice)
			.description("Episode purchase - ID: " + episodeId)
			.build();
		pointRepository.save(point);
	}

	// 장바구니 담기
	@Transactional
	public String addItemToCart(Long userId, Long episodeId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (cartItemRepository.existsByUserAndEpisode(user, episode)) {
			return "이미 담긴 회차입니다.";
		}

		// 장바구니 아이템 생성
		CartItem cartItem = new CartItem(user, episode); // 엔티티 생성자로 처리
		cartItemRepository.save(cartItem);

		return "회차가 장바구니에 성공적으로 담겼습니다.";
	}

	// 회차 추천
	@Transactional
	public String likeEpisode(Long userId, Long episodeId) {
		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// 에피소드 조회
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		// 이미 추천했는지 확인
		boolean exists = episodeLikeRepository.existsByUserAndEpisode(user, episode);
		if (exists) {
			return "이미 추천한 회차입니다.";
		}

		// 추천 추가
		EpisodeLike episodeLike = EpisodeLike.builder()
			.user(user)
			.episode(episode)
			.build();
		episodeLikeRepository.save(episodeLike);

		// 에피소드 추천 수 증가
		episode = Episode.builder()
			.episodeId(episode.getEpisodeId())
			.novel(episode.getNovel())
			.title(episode.getTitle())
			.viewCount(episode.getViewCount())
			.publishedAt(episode.getPublishedAt())
			.thumbnailRenamedFilename(episode.getThumbnailRenamedFilename())
			.thumbnailUrl(episode.getThumbnailUrl())
			.likeCount(episode.getLikeCount() + 1) // 추천 수 증가
			.content(episode.getContent())
			.status(episode.getStatus())
			.build();
		episodeRepository.save(episode);

		// 추천 취소 추가
		return "회차를 추천했습니다.";
	}

	//회차 목록
	public Page<EpisodeResponseDTO> getEpisodesByNovelId(Long novelId, int page, Sort.Direction direction) {

		PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(direction, "episodeId"));

		Page<Episode> episodePage = episodeRepository.getEpisodeList(novelId, pageRequest);

		return new PageImpl<>(
			episodePage.getContent().stream()
				.map(this::episodeToResponseDTO)
				.toList(),
			pageRequest,
			episodePage.getTotalElements()
		);
	}

	//소설 정보 조회 - user(작가) fetch join
	public MayPageNovelResponseDto findByNovelId(Long novelId) {

		Novel novel = novelRepository.findByNovelWithUser(novelId)
			.orElseThrow(() -> new IllegalArgumentException("소설 정보가 없습니다"));

		return novelToResponseDto(novel);
	}

	//convert

	private MayPageNovelResponseDto novelToResponseDto(Novel novel) {
		return MayPageNovelResponseDto.builder()
			.id(novel.getNovelId())
			.tag(novel.getTag().name())
			.title(novel.getTitle())
			.isFree(novel.getIsFree())
			.author(novel.getUser().getNickname())
			.likeCount(novel.getLikeCount())
			.imageUrl(novel.getThumbnailUrl())
			.favoriteCount(novel.getFavoriteCount())
			.description(novel.getDescription())
			.status(novel.getStatus().name())
			.build();
	}

	private EpisodeResponseDTO episodeToResponseDTO(Episode episode) {
		return EpisodeResponseDTO.builder()
			.episodeId(episode.getEpisodeId())
			.title(episode.getTitle())
			.viewCount(episode.getViewCount())
			.publishedAt(episode.getPublishedAt())
			.thumbnailUrl(episode.getThumbnailUrl())
			.content(episode.getContent())
			.status(episode.getStatus())
			.commentCount(episode.getCommentCount())
			.likeCount(episode.getLikeCount())
			.build();
	}

}
