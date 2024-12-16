package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.interstory.novel.domain.entity.Episode;

public interface EpisodeRepositoryCustom {
	Page<Episode> getEpisodeList(Long novelId, Pageable pageable);
}
