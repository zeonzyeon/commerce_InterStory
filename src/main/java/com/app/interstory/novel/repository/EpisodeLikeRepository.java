package com.app.interstory.novel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.EpisodeLike;
import com.app.interstory.user.domain.entity.User;

@Repository
public interface EpisodeLikeRepository extends JpaRepository<EpisodeLike, Long> {
	boolean existsByUserAndEpisode(User user, Episode episode);

	@Modifying
	@Query("DELETE FROM EpisodeLike el WHERE el.user = :user AND el.episode = :episode")
	void deleteByUserAndEpisode(@Param("user") User user, @Param("episode") Episode episode);
}
