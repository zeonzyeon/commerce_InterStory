package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.user.domain.entity.User;

@Repository
public interface NovelRepository extends JpaRepository<Novel, Long> {
	@Query("""
		SELECT n
		FROM Novel n
		LEFT JOIN Episode e ON n.novelId = e.novel.novelId
		WHERE n.user = :user
		GROUP BY n
		ORDER BY MAX(e.publishedAt) DESC
		""")
	Page<Novel> findNovelsSortedByLatestEpisode(@Param("user") User user, Pageable pageable);
}
