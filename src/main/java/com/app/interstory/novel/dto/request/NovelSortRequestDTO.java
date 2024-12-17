package com.app.interstory.novel.dto.request;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.user.domain.enumtypes.NovelSortType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NovelSortRequestDTO {
	private NovelSortType type;
	private MainTag mainTag;
}
