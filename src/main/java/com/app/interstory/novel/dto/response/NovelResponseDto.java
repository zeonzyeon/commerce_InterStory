package com.app.interstory.novel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NovelResponseDto {
	private Long id;
	private String title;
	private String author;
	private String description;
	private String imageUrl;
	private String status;
	private int favoriteCount;
	private int likeCount;
	private String tag;
	private boolean isFree;

}
