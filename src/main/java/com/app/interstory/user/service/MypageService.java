package com.app.interstory.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found id: " + id));
	}

	public UpdateUserResponseDTO updateUser(User user, UpdateUserRequestDTO updateUserRequestDTO) {
		user.update(
			updateUserRequestDTO.getProfileUrl(),
			updateUserRequestDTO.getNickname(),
			updateUserRequestDTO.getPassword()
		);

		userRepository.save(user);

		return new UpdateUserResponseDTO(user.getProfileUrl(), user.getNickname(), user.getPassword());
	}

	public List<FavoriteNovelResponseDTO> getFavoriteNovels(User user) {
		List<FavoriteNovel> favoriteNovels = favoriteNovelRepository.findByUser(user);

		return favoriteNovels.stream()
			.map(favoriteNovel -> {
				Novel novel = favoriteNovel.getNovel();

				Integer episodeCount = episodeRepository.countByNovel(novel);

				Integer lastReadEpisode = recentNovelRepository.findByUserAndNovel(user, novel)
					.map(recentNovel -> recentNovel.getEpisode().getEpisodeId().intValue())
					.orElse(0);

				Integer likeCount = episodeRepository.findByNovel(novel).stream()
					.mapToInt(Episode::getLikeCount)
					.sum();

				List<String> tags = tagRepository.findByNovel(novel).stream()
					.map(Tag::getTag)
					.toList();

				return FavoriteNovelResponseDTO.builder()
					.title(novel.getTitle())
					.author(novel.getUser().getNickname())
					.episodeCount(episodeCount)
					.likeCount(likeCount)
					.tags(tags)
					.thumbnailUrl(novel.getThumbnailUrl())
					.lastReadEpisode(lastReadEpisode)
					.build();
			})
			.toList();
	}
}
