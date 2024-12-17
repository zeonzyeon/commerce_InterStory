package com.app.interstory.novel.dto.request;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.domain.enumtypes.SortType;

import lombok.Getter;

@Getter
public class NovelSearchRequestDTO {
	private NovelStatus status;
	private String title;
	private String author;
	private Boolean monetized;
	private MainTag mainTag;
	private SortType sort;
	private int page;
	private int size;
}