package com.app.interstory.novel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.EpisodeLike;

@Repository
public interface EpisodeLikeRepository extends JpaRepository<EpisodeLike, Long> {
}
