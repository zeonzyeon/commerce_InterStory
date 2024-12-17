package com.app.interstory.novel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.novel.domain.entity.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
	boolean existsByUser_UserIdAndEpisode_EpisodeId(Long userId, Long episodeId);
}