package com.app.interstory.novel.repository;

import com.app.interstory.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.EpisodeLike;

import java.util.List;

@Repository
public interface EpisodeLikeRepository extends JpaRepository<EpisodeLike, Long> {
	boolean existsByUser_UserIdAndEpisode(Long userId, Episode episode);

	@Modifying
	@Query("DELETE FROM EpisodeLike el WHERE el.user.userId = :userId AND el.episode = :episode")
	void deleteByUserIdAndEpisode(@Param("userId") Long userId, @Param("episode") Episode episode);

    List<EpisodeLike> findByUser(User user);
}
