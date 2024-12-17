package com.app.interstory.novel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.dto.request.NovelSortRequestDTO;
import com.app.interstory.user.domain.enumtypes.NovelSortType;

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

	List<Novel> findPopularNovelsByTag(NovelSortRequestDTO request);

	List<Novel> findNovelByOrder(NovelSortType novelSortType);
}
