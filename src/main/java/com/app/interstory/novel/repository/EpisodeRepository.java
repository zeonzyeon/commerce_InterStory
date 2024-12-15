package com.app.interstory.novel.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long>, EpisodeRepositoryCustom {
	Integer countByNovel(Novel novel);

	@Query(value = """
		    SELECT 
		        ROW_NUMBER() OVER (PARTITION BY novel_id ORDER BY episode_id) AS row_number
		    FROM 
		        episode
		    WHERE 
		        novel_id = :novelId
		        AND episode_id = :episodeId
		""", nativeQuery = true)
	Map<String, Object> findRowNumberByNovelIdAndEpisodeId(@Param("novelId") Long novelId,
		@Param("episodeId") Long episodeId);

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
	Page<Episode> findEpisodesByNovelIdOrderByPublishedAtDesc(
		@Param("novelId") Long novelId,
		Pageable pageable
	);

	@Query("""
		    SELECT e
		    FROM Episode e
		    WHERE e.novel.novelId = :novelId
		    ORDER BY e.publishedAt ASC
		""")
	Page<Episode> findEpisodesByNovelIdOrderByPublishedAtAsc(
		@Param("novelId") Long novelId,
		Pageable pageable
	);
}
