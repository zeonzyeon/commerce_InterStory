package com.app.interstory.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.interstory.novel.domain.FavoriteNovel;
import com.app.interstory.novel.domain.Novel;
import com.app.interstory.novel.domain.Tag;
import com.app.interstory.novel.domain.Episode;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.FavoriteNovelRepository;
import com.app.interstory.novel.repository.RecentNovelRepository;
import com.app.interstory.novel.repository.TagRepository;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.FavoriteNovelResponseDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageService {
	private final UserRepository userRepository;
	private final EpisodeRepository episodeRepository;
	private final TagRepository tagRepository;
	private final FavoriteNovelRepository favoriteNovelRepository;
	private final RecentNovelRepository recentNovelRepository;

	public UpdateUserResponseDTO updateUser(User user, UpdateUserRequestDTO updateUserRequestDTO) {
		user.update(
			updateUserRequestDTO.getProfileUrl(),
			updateUserRequestDTO.getNickname(),
			updateUserRequestDTO.getPassword()
		);

		userRepository.save(user);

		return new UpdateUserResponseDTO(user.getProfileUrl(), user.getNickname(), user.getPassword());
	}

	public Page<FavoriteNovelResponseDTO> getFavoriteNovels(User user, Pageable pageable) {
		Page<FavoriteNovel> favoriteNovelPage = favoriteNovelRepository.findByUser(user, pageable);

		return favoriteNovelPage.map(favoriteNovel -> {
			Novel novel = favoriteNovel.getNovel();

			Integer episodeCount = episodeRepository.countByNovel(novel);

			Integer likeCount = episodeRepository.findByNovel(novel).stream()
				.mapToInt(Episode::getLikeCount)
				.sum();

			List<String> tags = tagRepository.findByNovel(novel).stream()
				.map(Tag::getTag)
				.toList();

			Integer lastReadEpisode = recentNovelRepository.findByUserAndNovel(user, novel)
				.map(recentNovel -> recentNovel.getEpisode().getEpisodeId().intValue())
				.orElse(0);

			return FavoriteNovelResponseDTO.builder()
				.title(novel.getTitle())
				.author(novel.getUser().getNickname())
				.episodeCount(episodeCount)
				.likeCount(likeCount)
				.tags(tags)
				.thumbnailUrl(novel.getThumbnailUrl())
				.lastReadEpisode(lastReadEpisode)
				.build();
		});
	}
}
