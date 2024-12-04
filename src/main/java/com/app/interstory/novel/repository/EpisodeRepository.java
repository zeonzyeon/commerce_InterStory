package com.app.interstory.novel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.novel.domain.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
}
