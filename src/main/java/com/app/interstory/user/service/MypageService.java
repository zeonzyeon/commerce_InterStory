package com.app.interstory.user.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Comment;
import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.FavoriteNovel;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.RecentNovel;
import com.app.interstory.novel.domain.entity.Tag;
import com.app.interstory.novel.repository.CommentRepository;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.FavoriteNovelRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.novel.repository.RecentNovelRepository;
import com.app.interstory.novel.repository.TagRepository;
import com.app.interstory.payment.domain.entity.Subscribe;
import com.app.interstory.payment.domain.repository.SubscribeRepository;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.Coupon;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.Settlement;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.domain.entity.UserCoupon;
import com.app.interstory.user.dto.request.AccountRequestDTO;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.AccountResponseDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MyCommentResponseDTO;
import com.app.interstory.user.dto.response.MyNovelResponseDTO;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.dto.response.PointHistoryResponseDTO;
import com.app.interstory.user.dto.response.ReadNovelResponseDTO;
import com.app.interstory.user.dto.response.SettlementResponseDTO;
import com.app.interstory.user.dto.response.SubscriptionResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.dto.response.UserCouponResponseDTO;
import com.app.interstory.user.repository.CouponRepository;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.SettlementRepository;
import com.app.interstory.user.repository.UserCouponRepository;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageService {
	private static final String NOT_FOUND_USER_ID = "해당 사용자가 없습니다. id : ";
	private final UserRepository userRepository;
	private final EpisodeRepository episodeRepository;
	private final TagRepository tagRepository;
	private final FavoriteNovelRepository favoriteNovelRepository;
	private final RecentNovelRepository recentNovelRepository;
	private final PointRepository pointRepository;
	private final NovelRepository novelRepository;
	private final SettlementRepository settlementRepository;
	private final CommentRepository commentRepository;
	private final SubscribeRepository subscribeRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponRepository couponRepository;

	public MypageResponseDTO getUser(CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getUserId();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		return new MypageResponseDTO(
			user.getNickname(),
			user.getProfileUrl(),
			user.getPoint(),
			user.getIsSubscribe(),
			user.getIsAutoPayment()
		);
	}

	@Transactional
	public UpdateUserResponseDTO updateUser(CustomUserDetails userDetails, UpdateUserRequestDTO updateUserRequestDTO) {
		Long userId = userDetails.getUser().getUserId();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		user.update(
			updateUserRequestDTO.getProfileUrl(),
			updateUserRequestDTO.getNickname(),
			updateUserRequestDTO.getPassword()
		);

		return new UpdateUserResponseDTO(user.getProfileUrl(), user.getNickname(), user.getPassword());
	}

	public Page<FavoriteNovelResponseDTO> getFavoriteNovels(CustomUserDetails userDetails, Pageable pageable) {
		Long userId = userDetails.getUser().getUserId();

		Page<FavoriteNovel> favoriteNovelPage = favoriteNovelRepository.findFavoritesSortedByLatestEpisode(userId,
			pageable);
		// 만약 관심작품 선정은 했지만 한번도 열람하지 않았을 경우 EpisodeId는 0으로 처리됨

		return favoriteNovelPage.map(favoriteNovel -> {
			Novel novel = favoriteNovel.getNovel();

			Integer episodeCount = episodeRepository.countByNovel(novel);

			List<String> tags = tagRepository.findByNovel(novel).stream()
				.map(Tag::getTag)
				.toList();

			Long lastReadEpisodeId = recentNovelRepository.findByUser_UserIdAndNovel(userId, novel)
				.map(recentNovel -> recentNovel.getEpisode().getEpisodeId())
				.orElse(0L);

			Map<String, Object> getLastReadEpisodeNumber = episodeRepository.findRowNumberByNovelIdAndEpisodeId(
				novel.getNovelId(), lastReadEpisodeId);

			int lastReadEpisodeNumber;
			if (getLastReadEpisodeNumber != null && getLastReadEpisodeNumber.containsKey("row_number")) {
				lastReadEpisodeNumber = Integer.parseInt(getLastReadEpisodeNumber.get("row_number").toString());
			} else {
				lastReadEpisodeNumber = 0;
			}

			return FavoriteNovelResponseDTO.builder()
				.title(novel.getTitle())
				.author(novel.getUser().getNickname())
				.episodeCount(episodeCount)
				.likeCount(novel.getLikeCount())
				.tags(tags)
				.thumbnailUrl(novel.getThumbnailUrl())
				.lastReadEpisodeId(lastReadEpisodeId)
				.lastReadEpisodeNumber(lastReadEpisodeNumber)
				.build();
		});
	}

	public Page<ReadNovelResponseDTO> getReadNovels(CustomUserDetails userDetails, Pageable pageable) {
		Long userId = userDetails.getUser().getUserId();

		Page<RecentNovel> recentNovelPage = recentNovelRepository.findByUser_UserId(userId, pageable);

		return recentNovelPage.map(readNovel -> {
			Novel novel = readNovel.getNovel();

			Integer episodeCount = episodeRepository.countByNovel(novel);

			List<String> tags = tagRepository.findByNovel(novel).stream()
				.map(Tag::getTag)
				.toList();

			Integer lastReadEpisode = recentNovelRepository.findByUser_UserIdAndNovel(userId, novel)
				.map(recentNovel -> recentNovel.getEpisode().getEpisodeId().intValue())
				.orElse(0);

			return ReadNovelResponseDTO.builder()
				.title(novel.getTitle())
				.author(novel.getUser().getNickname())
				.episodeCount(episodeCount)
				.likeCount(novel.getLikeCount())
				.tags(tags)
				.thumbnailUrl(novel.getThumbnailUrl())
				.lastReadEpisode(lastReadEpisode)
				.build();
		});
	}

	public Page<PointHistoryResponseDTO> getPointHistory(CustomUserDetails userDetails, Pageable pageable) {
		Long userId = userDetails.getUser().getUserId();

		Page<Point> pointPage = pointRepository.findByUser_UserId(userId, pageable);

		return pointPage.map(pointHistory -> {
			String pointChange;
			if (pointHistory.getBalance() > 0) {
				pointChange = pointHistory.getBalance() + "P 충전";
			} else {
				pointChange = Math.abs(pointHistory.getBalance()) + "P 사용";
			}

			return PointHistoryResponseDTO.builder()
				.pointChange(pointChange)
				.description(pointHistory.getDescription())
				.date(pointHistory.getUsedAt())
				.build();
		});
	}

	public Page<MyNovelResponseDTO> getMyNovels(CustomUserDetails userDetails, Pageable pageable) {
		Long userId = userDetails.getUser().getUserId();

		Page<Novel> novelPage = novelRepository.findNovelsSortedByLatestEpisode(userId, pageable);

		return novelPage.map(myNovel -> {

			List<String> tags = tagRepository.findByNovel(myNovel).stream()
				.map(Tag::getTag)
				.toList();

			return MyNovelResponseDTO.builder()
				.novelId(myNovel.getNovelId())
				.title(myNovel.getTitle())
				.likeCount(myNovel.getLikeCount())
				.tags(tags)
				.thumbnailUrl(myNovel.getThumbnailUrl())
				.episodeUpdatedAt(myNovel.getEpisodeUpdatedAt())
				.favoriteCount(myNovel.getFavoriteCount())
				.build();
		});
	}

	public SettlementResponseDTO getSettlement(CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getUserId();

		Settlement settlement = settlementRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		return SettlementResponseDTO.builder()
			.fee(settlement.getFee())
			.build();
	}

	@Transactional
	public void requestSettlement(CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getUserId();

		Settlement settlement = settlementRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		settlement.requestSettlement();
	}

	public Page<MyCommentResponseDTO> getMyComments(CustomUserDetails userDetails, Pageable pageable) {
		Long userId = userDetails.getUser().getUserId();

		Page<Comment> myCommentPage = commentRepository.findByUser_UserId(userId, pageable);

		return myCommentPage.map(myComment -> {
			Novel novel = myComment.getEpisode().getNovel();
			Episode episode = myComment.getEpisode();

			Map<String, Object> getEpisodeNumber = episodeRepository.findRowNumberByNovelIdAndEpisodeId(
				novel.getNovelId(), episode.getEpisodeId());

			long episodeNumber;
			if (getEpisodeNumber != null && getEpisodeNumber.containsKey("row_number")) {
				episodeNumber = Integer.parseInt(getEpisodeNumber.get("row_number").toString());
			} else {
				episodeNumber = 0L;
			}

			return MyCommentResponseDTO.builder()
				.novelTitle(novel.getTitle())
				.episodeNumber(episodeNumber)
				.content(myComment.getContent())
				.createdAt(myComment.getCreatedAt())
				.likeCount(myComment.getLikeCount())
				.build();
		});
	}

	public AccountResponseDTO getAccount(CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getUserId();

		Settlement settlement = settlementRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		return AccountResponseDTO.builder()
			.accountNumber(settlement.getAccountNumber())
			.build();
	}

	@Transactional
	public AccountResponseDTO updateAccount(CustomUserDetails userDetails, AccountRequestDTO accountRequestDTO) {
		Long userId = userDetails.getUser().getUserId();

		Settlement settlement = settlementRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		settlement.updateAccountNumber(accountRequestDTO.getAccountNumber());

		return AccountResponseDTO.builder()
			.accountNumber(settlement.getAccountNumber())
			.build();
	}

	public Page<UserCouponResponseDTO> getCoupons(CustomUserDetails userDetails, Pageable pageable) {
		Long userId = userDetails.getUser().getUserId();

		Page<UserCoupon> couponPage = userCouponRepository.findByUser_UserId(userId, pageable);

		return couponPage.map(UserCouponResponseDTO::from);
	}

	public UserCouponResponseDTO saveCoupon(CustomUserDetails userDetails, String couponCode) {
		Long userId = userDetails.getUser().getUserId();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		Coupon coupon = couponRepository.findByCode(couponCode)
			.orElseThrow(() -> new IllegalArgumentException("해당 쿠폰이 존재하지 않습니다."));

		UserCoupon userCoupon = UserCoupon.builder()
			.user(user)
			.coupon(coupon)
			.build();

		userCouponRepository.save(userCoupon);

		return UserCouponResponseDTO.from(userCoupon);
	}

	public SubscriptionResponseDTO getSubscription(CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getUserId();

		Subscribe subscribe = subscribeRepository.findByUser_UserId(userId);

		Timestamp endAt;

		if (subscribe == null) {
			endAt = null;
		} else {
			endAt = subscribe.getEndAt();
		}

		return SubscriptionResponseDTO.builder()
			.endAt(endAt)
			.build();
	}
}
