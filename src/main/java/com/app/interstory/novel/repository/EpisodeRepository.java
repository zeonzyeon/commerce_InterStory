package com.app.interstory.novel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
	Integer countByNovel(Novel novel);

	Optional<Episode> findByNovel(Novel novel);
}
