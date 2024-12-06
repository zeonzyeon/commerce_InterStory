package com.app.interstory.episode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.novel.domain.entity.Collection;
import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.user.domain.entity.User;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
	boolean existsByUserAndEpisode(User user, Episode episode);
}