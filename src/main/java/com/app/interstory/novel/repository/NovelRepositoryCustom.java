package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.interstory.novel.domain.entity.Novel;

public interface NovelRepositoryCustom {
	Page<Novel> findAllWithFiltersAndSort(
		Long userId,
		String status,
		String title,
		String author,
		Boolean monetized,
		String tag,
		String sort,
		Pageable pageable
	);
}

