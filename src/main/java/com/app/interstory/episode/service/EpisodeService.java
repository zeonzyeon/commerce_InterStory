package com.app.interstory.episode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.episode.domain.dto.EpisodeRequestDTO;
import com.app.interstory.episode.domain.dto.EpisodeResponseDTO;
import com.app.interstory.episode.repository.CartItemRepository;
import com.app.interstory.episode.repository.CollectionRepository;
import com.app.interstory.episode.repository.PointRepository;
import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.UserRepository;

@Service
public class EpisodeService {
	private final NovelRepository novelRepository;
	private final EpisodeRepository episodeRepository;
	private final UserRepository userRepository;
	private final CollectionRepository collectionRepository;
	private final PointRepository pointRepository;
	private final CartItemRepository cartItemRepository;

	public EpisodeService(NovelRepository novelRepository, EpisodeRepository episodeRepository,
		UserRepository userRepository,
		CollectionRepository collectionRepository, PointRepository pointRepository,
		CartItemRepository cartItemRepository) {
		this.novelRepository = novelRepository;
		this.episodeRepository = episodeRepository;
		this.userRepository = userRepository;
		this.collectionRepository = collectionRepository;
		this.pointRepository = pointRepository;
		this.cartItemRepository = cartItemRepository;
	}

	// 회차 작성
	@Transactional
	public EpisodeResponseDTO writeEpisode(Long novelId, EpisodeRequestDTO requestDTO) {
		// Novel 엔티티 조회
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found"));

		// Episode 엔티티 생성
		Episode episode = Episode.builder()
			.novel(novel)
			.title(requestDTO.getTitle())
			.thumbnailRenamedFilename(requestDTO.getThumbnailRenamedFilename())
			.thumbnailUrl(requestDTO.getThumbnailUrl())
			.content(requestDTO.getContent())
			.status(requestDTO.getStatus() != null ? requestDTO.getStatus() : true)
			.build();

		episodeRepository.save(episode);

		return convertToDTO(episode);
	}

	// 회차 수정
	public EpisodeResponseDTO updateEpisode(Long novelId, Long episodeId, EpisodeRequestDTO requestDTO) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovelId().equals(novelId)) {
			throw new IllegalArgumentException("Invalid novelId for the given episode.");
		}

		episode.setTitle(requestDTO.getTitle());
		episode.setThumbnailRenamedFilename(requestDTO.getThumbnailRenamedFilename());
		episode.setThumbnailUrl(requestDTO.getThumbnailUrl());
		episode.setContent(requestDTO.getContent());
		episode.setStatus(requestDTO.getStatus());

		Episode updatedEpisode = episodeRepository.save(episode);
		return convertToDTO(updatedEpisode);
	}

	// 회차 상세 조회
	public EpisodeResponseDTO readEpisode(Long novelId, Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovelId().equals(novelId)) {
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
	public void deleteEpisode(Long novelId, Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovelId().equals(novelId)) {
			throw new RuntimeException("Episode does not belong to the specified novel");
		}

		episodeRepository.delete(episode);
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

		if (!episode.getNovelId().equals(novelId)) {
			throw new RuntimeException("Invalid novel ID for the given episode.");
		}

		// 3. 에피소드 가격 정의
		Long episodePrice = 500L; // 임시 포인트

		// 4. 포인트 확인
		if (user.getPoint() < episodePrice) {
			throw new RuntimeException("Insufficient points");
		}

		// 5. 포인트 차감
		user.setPoint(user.getPoint() - episodePrice);
		userRepository.save(user);

		// 6. 포인트 사용 내역 저장
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
		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// 에피소드 조회
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		// 동일한 에피소드가 이미 장바구니에 담겼는지 확인
		boolean exists = cartItemRepository.existsByUserAndEpisode(user, episode);

		if (exists) {
			return "이미 담긴 회차입니다.";
		}

		// 장바구니에 새로운 에피소드 추가
		CartItem cartItem = CartItem.builder()
			.user(user)
			.episode(episode)
			.build();

		cartItemRepository.save(cartItem);

		return "회차가 장바구니에 성공적으로 담겼습니다.";
	}
}
