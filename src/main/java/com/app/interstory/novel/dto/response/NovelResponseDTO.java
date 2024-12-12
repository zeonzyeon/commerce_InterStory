package com.app.interstory.novel.dto.response;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NovelResponseDTO {
	private Long novelId;
	private String title;
	private String description;
	private String thumbnailUrl;
	private Integer likeCount;
	private NovelStatus status;
	private MainTag tag;
}
