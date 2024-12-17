package com.app.interstory.novel.dto.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NovelEpisodeResponseDTO {
	private Long episodeId;
	private Long novelId;
	private String title;
	private Integer viewCount;
	private Timestamp publishedAt;
	private String thumbnailUrl;
	private Integer likeCount;
	private Boolean status;
	private Boolean isPurchased;
	private Boolean isFree;
	private Integer commentCount;
}
