package com.app.interstory.novel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.EpisodeLike;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.dto.request.EpisodeRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.repository.EpisodeLikeRepository;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.CartItemRepository;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EpisodeService {
	private final NovelRepository novelRepository;
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
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovel().getNovelId().equals(novelId)) {
			throw new RuntimeException("Invalid novel ID for the given episode.");
		}

		Long episodePrice = 5L;

		user.reducePointsForPurchase(episodePrice);

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

		CartItem cartItem = new CartItem(user, episode);
		cartItemRepository.save(cartItem);

		return "회차가 장바구니에 성공적으로 담겼습니다.";
	}

	// 회차 추천
	@Transactional
	public String likeEpisode(Long userId, Long episodeId) {
		String afterLikeMessage;

		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (episodeLikeRepository.existsByUser_UserIdAndEpisode(userId, episode)) {
			episodeLikeRepository.deleteByUserIdAndEpisode(userId, episode);
			episode.decrementLikeCount();
			afterLikeMessage = "회차 추천이 취소되었습니다.";
		} else {
			EpisodeLike episodeLike = EpisodeLike.builder()
				.user(userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("User not found")))
				.episode(episode)
				.build();
			episodeLikeRepository.save(episodeLike);
			episode.incrementLikeCount();
			afterLikeMessage = "회차를 추천했습니다.";
		}

		episodeRepository.save(episode);

		return afterLikeMessage;
	}
}
