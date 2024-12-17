package com.app.interstory.novel.domain.enumtypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SortType {
	OLD_TO_NEW("오래된순"),
	NEW_TO_OLD("최신순"),
	RECOMMENDATION("추천순");

	private final String description;
}