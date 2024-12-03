package com.app.interstory.episode.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EpisodeResponseDTO {
	private Long episodeId;
	private Long novelId;
	private String title;
	private int viewCount;
	private Timestamp publishedAt;
	private String thumbnailUrl;
	private int likeCount;
	private String content;
	private String status;
}
