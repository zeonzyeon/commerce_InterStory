package com.app.interstory.novel.dto.request;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;

import lombok.Getter;

import java.util.List;

@Getter
public class NovelRequestDTO {
	private String title;
	private String description;
	private String plan;
	private MainTag tag;
	private Boolean isFree;
	private List<String> customTag;
}