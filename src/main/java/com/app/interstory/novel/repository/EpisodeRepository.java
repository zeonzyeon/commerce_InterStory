package com.app.interstory.novel.repository;

import com.app.interstory.novel.domain.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
}
