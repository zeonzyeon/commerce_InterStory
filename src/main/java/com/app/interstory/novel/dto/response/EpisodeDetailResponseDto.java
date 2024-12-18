package com.app.interstory.novel.dto.response;

import com.app.interstory.novel.domain.entity.Episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDetailResponseDto {

	private String title;
	private String content;
	private Integer likeCount;
	private Integer commentCount;
	private Integer viewCount;
	private Integer episodeNumber;

	public static EpisodeDetailResponseDto from(Episode episode) {
		return EpisodeDetailResponseDto.builder()
			.title(episode.getTitle())
			.content(episode.getContent())
			.episodeNumber(episode.getEpisodeNumber())
			.commentCount(episode.getCommentCount())
			.likeCount(episode.getLikeCount())
			.viewCount(episode.getViewCount())
			.build();
	}
}
