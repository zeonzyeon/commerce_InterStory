package com.app.interstory.novel.repository;

import java.util.Optional;
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

	Optional<Novel> findByNovelWithUser(Long novelId);

}
