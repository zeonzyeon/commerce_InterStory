package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;

@Repository
public interface NovelRepository extends JpaRepository<Novel, Long>, NovelRepositoryCustom {
	@Query("""
		SELECT n
		FROM Novel n
		LEFT JOIN Episode e ON n.novelId = e.novel.novelId
		WHERE n.user.userId = :userId
		GROUP BY n
		ORDER BY MAX(e.publishedAt) DESC
		""")
	Page<Novel> findNovelsSortedByLatestEpisode(@Param("userId") Long userId, Pageable pageable);

	@Query("""
		    SELECT e
		    FROM Episode e
		    WHERE e.novel.novelId = :novelId
		    ORDER BY e.likeCount DESC
		""")
	Page<Episode> findEpisodesByNovelIdOrderByLikeCount(
		@Param("novelId") Long novelId,
		Pageable pageable
	);

	@Query("""
		    SELECT e
		    FROM Episode e
		    WHERE e.novel.novelId = :novelId
		    ORDER BY e.publishedAt DESC
		""")
	Page<Episode> findEpisodesByNovelIdOrderByPublishedAt(
		@Param("novelId") Long novelId,
		Pageable pageable
	);

	@Query("""
		    SELECT n
		    FROM Novel n
		    LEFT JOIN Episode e ON n.novelId = e.novel.novelId
		    WHERE (:userId IS NULL OR n.user.userId = :userId)
		    AND (:status IS NULL OR n.status = :status)
		    AND (:title IS NULL OR n.title LIKE %:title%)
		    AND (:author IS NULL OR n.user.nickname LIKE %:author%)
		    AND (:monetized IS NULL OR n.isFree = :monetized)
		    AND (:tag IS NULL OR n.tag = :tag)
		    GROUP BY n.novelId
		    ORDER BY 
		        CASE WHEN :sort = 'recommendations' THEN n.likeCount END DESC, 
		        CASE WHEN :sort = 'latest' THEN MAX(e.publishedAt) END DESC
		""")
	Page<Novel> findAllWithDynamicSort(
		@Param("userId") Long userId,
		@Param("status") NovelStatus status,
		@Param("title") String title,
		@Param("author") String author,
		@Param("monetized") Boolean monetized,
		@Param("tag") MainTag tag,
		@Param("sort") String sort,
		Pageable pageable
	);
}
