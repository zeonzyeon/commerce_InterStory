package com.app.interstory.novel.dto.request;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;

import lombok.Getter;

@Getter
public class NovelRequestDTO {
	private Long userId;
	private String title;
	private String description;
	private String plan;
	private String thumbnailRenamedFilename;
	private String thumbnailUrl;
	private MainTag tag;
	private NovelStatus status;
	private Boolean isFree;

}