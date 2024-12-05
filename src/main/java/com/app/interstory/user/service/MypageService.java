package com.app.interstory.user.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.interstory.novel.domain.entity.FavoriteNovel;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.RecentNovel;
import com.app.interstory.novel.domain.entity.Tag;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.FavoriteNovelRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.novel.repository.RecentNovelRepository;
import com.app.interstory.novel.repository.TagRepository;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.MyNovelResponseDTO;
import com.app.interstory.user.dto.response.MypageResponseDTO;
import com.app.interstory.user.dto.response.PointHistoryResponseDTO;
import com.app.interstory.user.dto.response.ReadNovelResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageService {
	private final UserRepository userRepository;
	private final EpisodeRepository episodeRepository;
	private final TagRepository tagRepository;
	private final FavoriteNovelRepository favoriteNovelRepository;
	private final RecentNovelRepository recentNovelRepository;
	private final PointRepository pointRepository;
	private final NovelRepository novelRepository;

	public MypageResponseDTO getUser(CustomUserDetails userDetails) {
		User user = userDetails.getUser();

		return new MypageResponseDTO(
			user.getNickname(),
			user.getProfileUrl(),
			user.getPoint(),
			user.getSubscribe(),
			user.getAutoPayment()
		);
	}

	public UpdateUserResponseDTO updateUser(CustomUserDetails userDetails, UpdateUserRequestDTO updateUserRequestDTO) {
		User user = userDetails.getUser();

		user.update(
			updateUserRequestDTO.getProfileUrl(),
			updateUserRequestDTO.getNickname(),
			updateUserRequestDTO.getPassword()
		);

		userRepository.save(user);

		return new UpdateUserResponseDTO(user.getProfileUrl(), user.getNickname(), user.getPassword());
	}

	public Page<FavoriteNovelResponseDTO> getFavoriteNovels(CustomUserDetails userDetails, Pageable pageable) {
		User user = userDetails.getUser();

		Page<FavoriteNovel> favoriteNovelPage = favoriteNovelRepository.findFavoritesSortedByLatestEpisode(user, pageable);
		// 만약 관심작품 선정은 했지만 한번도 열람하지 않았을 경우 EpisodeId는 0으로 처리됨

		return favoriteNovelPage.map(favoriteNovel -> {
			Novel novel = favoriteNovel.getNovel();

			Integer episodeCount = episodeRepository.countByNovel(novel);

			List<String> tags = tagRepository.findByNovel(novel).stream()
				.map(Tag::getTag)
				.toList();

			Long lastReadEpisodeId = recentNovelRepository.findByUserAndNovel(user, novel)
				.map(recentNovel -> recentNovel.getEpisode().getEpisodeId())
				.orElse(0L);

			Map<String, Object> getLastReadEpisodeNumber = episodeRepository.findRowNumberByNovelIdAndEpisodeId(novel.getNovelId(), lastReadEpisodeId);

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
		User user = userDetails.getUser();

		Page<RecentNovel> recentNovelPage = recentNovelRepository.findByUser(user, pageable);

		return recentNovelPage.map(readNovel -> {
			Novel novel = readNovel.getNovel();

			Integer episodeCount = episodeRepository.countByNovel(novel);

			List<String> tags = tagRepository.findByNovel(novel).stream()
				.map(Tag::getTag)
				.toList();

			Integer lastReadEpisode = recentNovelRepository.findByUserAndNovel(user, novel)
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
		User user = userDetails.getUser();

		Page<Point> pointPage = pointRepository.findByUser(user, pageable);

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
		User user = userDetails.getUser();

		Page<Novel> novelPage = novelRepository.findNovelsSortedByLatestEpisode(user, pageable);

		return novelPage.map(myNovel -> {

			List<String> tags = tagRepository.findByNovel(myNovel).stream()
				.map(Tag::getTag)
				.toList();

			return MyNovelResponseDTO.builder()
				.title(myNovel.getTitle())
				.likeCount(myNovel.getLikeCount())
				.tags(tags)
				.thumbnailUrl(myNovel.getThumbnailUrl())
				// TODO: REDIS로부터 작품 반응 조회
				.build();
		});
	}
}
