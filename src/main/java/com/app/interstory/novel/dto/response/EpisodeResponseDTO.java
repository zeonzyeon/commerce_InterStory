package com.app.interstory.novel.dto.response;

import java.sql.Timestamp;

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
	private String title;
	private Integer viewCount;
	private Integer commentCount;
	private Timestamp publishedAt;
	private String thumbnailUrl;
	private Integer likeCount;
	private String content;
	private Boolean status;

}
