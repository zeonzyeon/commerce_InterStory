package com.app.interstory.novel.dto.response;

import java.sql.Timestamp;

import com.app.interstory.novel.domain.entity.Episode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EpisodeResponseDTO {
	private Long episodeId;
	private Long novelId;
	private String title;
	private Integer viewCount;
	private Integer commentCount;
	private Timestamp publishedAt;
	private String thumbnailUrl;
	private Integer likeCount;
	private String content;
	private Boolean status;

	public static EpisodeResponseDTO fromEpisode(Episode episode) {
		return EpisodeResponseDTO.builder()
			.episodeId(episode.getEpisodeId())
			.novelId(episode.getNovel().getNovelId())
			.title(episode.getTitle())
			.viewCount(episode.getViewCount())
			.commentCount(episode.getCommentCount())
			.publishedAt(episode.getPublishedAt())
			.thumbnailUrl(episode.getThumbnailUrl())
			.likeCount(episode.getLikeCount())
			.content(episode.getContent())
			.status(episode.getStatus())
			.build();
	}
}
