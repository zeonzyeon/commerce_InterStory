package com.app.interstory.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.episode.domain.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Integer> {
	boolean existsByUserIdAndEpisodeId(Long userId, Long episodeId);
}
