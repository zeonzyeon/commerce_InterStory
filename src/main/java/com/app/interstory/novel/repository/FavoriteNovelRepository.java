package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.FavoriteNovel;

@Repository
public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, Long> {
	@Query("""
		SELECT fn
		FROM FavoriteNovel fn
		JOIN FETCH fn.novel n
		LEFT JOIN Episode e ON n.novelId = e.novel.novelId
		WHERE fn.user.userId = :userId
		GROUP BY fn
		ORDER BY MAX(e.publishedAt) DESC
		""")
	Page<FavoriteNovel> findFavoritesSortedByLatestEpisode(@Param("userId") Long userId, Pageable pageable);

}
