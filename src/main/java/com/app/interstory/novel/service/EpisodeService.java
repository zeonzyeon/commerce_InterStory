package com.app.interstory.novel.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Collection;
import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.EpisodeLike;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.enumtypes.SortType;
import com.app.interstory.novel.dto.request.EpisodeRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeDetailResponseDto;
import com.app.interstory.novel.dto.response.EpisodeListResponseDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.dto.response.MyPageNovelResponseDto;
import com.app.interstory.novel.dto.response.NovelEpisodeResponseDTO;
import com.app.interstory.novel.repository.CollectionRepository;
import com.app.interstory.novel.repository.EpisodeLikeRepository;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.payment.domain.enumtypes.PaymentType;
import com.app.interstory.payment.service.KakaoService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.CartItem;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.Settlement;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.CartItemRepository;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EpisodeService {
	private final NovelRepository novelRepository;
	private final EpisodeRepository episodeRepository;
	private final UserRepository userRepository;
	private final PointRepository pointRepository;
	private final CartItemRepository cartItemRepository;
	private final EpisodeLikeRepository episodeLikeRepository;
	private final CollectionRepository collectionRepository;
	private final KakaoService kakaoService;
	private final RedisTemplate<String, String> redisTemplate;

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
	public EpisodeResponseDTO updateEpisode(Long episodeId, EpisodeRequestDTO requestDTO) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		episode.updateEpisode(requestDTO);

		return convertToDTO(episode);
	}

	// // 회차 상세 조회 변경
	// public EpisodeResponseDTO readEpisode(Long episodeId) {
	// 	Episode episode = episodeRepository.findById(episodeId)
	// 		.orElseThrow(() -> new RuntimeException("Episode not found"));
	//
	// 	return convertToDTO(episode);
	// }

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
	public void deleteEpisode(Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		episode.markAsDeleted();
	}

	// 회차 구매
	@Transactional
	public void purchaseEpisode(Long userId, Long episodeId) {
		// 1. 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// 2. 에피소드 조회 및 검증
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		Collection collection = Collection.builder()
			.user(user)
			.episode(episode)
			.build();

		collectionRepository.save(collection);

		Long episodePrice = 5L;

		// 4. 포인트 차감
		user.reducePointsForPurchase(episodePrice);

		// 5. 포인트 사용 내역 저장
		Point point = Point.builder()
			.user(user)
			.balance(-episodePrice)
			.description("회차 구매  : " + episode.getTitle() + "사용 포인트 :" + episodePrice)
			.build();
		pointRepository.save(point);

		if (user.getPoint() < 50L && user.getIsAutoPayment()) {
			kakaoService.kakaoPayPayment(userId, PaymentType.AUTO);
		}
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
		String afterLikeMessage;

		// 에피소드 조회
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

	public EpisodeListResponseDTO getEpisodeList(CustomUserDetails userDetails, Long novelId, SortType sort,
		Pageable pageable, boolean showAll) {
		Long userId = userDetails.getUser().getUserId();

		Page<Episode> episodes;
		if (sort == SortType.OLD_TO_NEW) {
			episodes = episodeRepository.findEpisodesByNovelIdOrderByPublishedAtAsc(novelId, pageable);
		} else {
			episodes = episodeRepository.findEpisodesByNovelIdOrderByPublishedAtDesc(novelId, pageable);
		}

		List<NovelEpisodeResponseDTO> episodeDTOs = episodes.getContent().stream()
			.map(episode -> {
				Long episodeId = episode.getEpisodeId();
				boolean isFree = novelRepository.findById(novelId)
					.orElseThrow(() -> new RuntimeException("Novel not found"))
					.getIsFree();

				return NovelEpisodeResponseDTO.builder()
					.episodeId(episodeId)
					.novelId(novelId)
					.title(episode.getTitle())
					.viewCount(episode.getViewCount())
					.publishedAt(episode.getPublishedAt())
					.thumbnailUrl(episode.getThumbnailUrl())
					.likeCount(episode.getLikeCount())
					.status(episode.getStatus())
					.isPurchased(collectionRepository.existsByUser_UserIdAndEpisode_EpisodeId(userId, episodeId))
					.isFree(isFree || episodes.getContent().stream()
						.sorted(Comparator.comparing(Episode::getPublishedAt))
						.toList()
						.indexOf(episode) < 5)
					.commentCount(episode.getCommentCount())
					.build();
			})
			.toList();

		return EpisodeListResponseDTO.builder()
			.episodeList(episodeDTOs)
			.totalPage(episodes.getTotalPages())
			.showAll(showAll)
			.build();
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
	public MyPageNovelResponseDto findByNovelId(Long novelId) {

		Novel novel = novelRepository.findByNovelWithUser(novelId)
			.orElseThrow(() -> new IllegalArgumentException("소설 정보가 없습니다"));

		return novelToResponseDto(novel);
	}

	@Transactional
	public EpisodeDetailResponseDto viewEpisodeDetail(Long episodeId, HttpServletRequest request) {

		//ip addr
		String ip = request.getRemoteAddr();

		Episode episodeDetail = episodeRepository.findEpisodeWithNovelAndUserAndSettlement(episodeId)
			.orElseThrow(() -> new EntityNotFoundException("Episode not found with id: " + episodeId));
		Novel thisNovel = episodeDetail.getNovel();
		User author = episodeDetail.getNovel().getUser();
		Settlement settlement = author.getSettlement();

		//true 면 조회수 증가
		if (isFirstView(ip, episodeId)) {
			if (settlement == null) {
				settlement = createSettlement(author);
				author.addSettlement(settlement);
			}
			if (thisNovel.getIsFree() || episodeDetail.getEpisodeNumber() >= 4) {
				settlement.addViewCount(true);
			} else {
				settlement.addViewCount(false);
			}
		}

		return EpisodeDetailResponseDto.from(episodeDetail);
	}

	private Settlement createSettlement(User author) {
		return Settlement.builder()
			.user(author)
			.build();
	}

	private boolean isFirstView(String ip, Long episodeId) {
		//TTL 설정 - 6시간
		int expireTime = 6 * 60 * 60;
		//key 설정 - id + yyyyMMdd
		String key = String.format("view:%d:%s",
			episodeId,
			LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
		);

		// IP를 해시화하여 비트맵의 오프셋으로 사용
		long offset = Math.abs(ip.hashCode() % 10000000);

		// setbit 명령어로 조회 여부 체크 및 설정
		Boolean result = redisTemplate.opsForValue()
			.getBit(key, offset);

		redisTemplate.opsForValue().setBit(key, offset, true);

		return !Boolean.TRUE.equals(result);

	}

	//convert
	private MyPageNovelResponseDto novelToResponseDto(Novel novel) {
		return MyPageNovelResponseDto.builder()
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
