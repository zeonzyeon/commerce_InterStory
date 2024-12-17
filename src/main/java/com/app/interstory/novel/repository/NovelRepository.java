package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
		    SELECT n
		    FROM Novel n
		    LEFT JOIN Episode e ON n.novelId = e.novel.novelId
		    WHERE (:status IS NULL OR n.status = :status)
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
		@Param("status") NovelStatus status,
		@Param("title") String title,
		@Param("author") String author,
		@Param("monetized") Boolean monetized,
		@Param("tag") MainTag tag,
		@Param("sort") String sort,
		Pageable pageable
	);

	@Query("SELECT n FROM Novel n "
		+ "WHERE (:status IS NULL OR n.status = :status) "
		+ "AND (:title IS NULL OR n.title LIKE %:title%) "
		+ "AND (:author IS NULL OR n.user.nickname LIKE %:author%) "
		+ "AND (:monetized IS NULL OR n.isFree = :monetized) "
		+ "AND (:tag IS NULL OR n.tag = :tag)"
		+ " ORDER BY "
		+ "CASE WHEN :sort = '오래된순' THEN n.publishedAt END ASC, "
		+ "CASE WHEN :sort = '최신순' THEN n.publishedAt END DESC, "
		+ "CASE WHEN :sort = '추천순' THEN n.likeCount END DESC")
	Page<Novel> findByFilterAndSort(
		@Param("status") NovelStatus status,
		@Param("title") String title,
		@Param("author") String author,
		@Param("monetized") Boolean monetized,
		@Param("tag") MainTag tag,
		@Param("sort") String sort,
		Pageable pageable
	);
	
}
