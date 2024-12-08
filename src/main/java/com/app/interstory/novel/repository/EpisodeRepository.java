package com.app.interstory.novel.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
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

	// 회차 리스트 최신순 정렬
	List<Episode> findByNovelOrderByPublishedAtDesc(Novel novel);

	// 회차 리스트 추천수 정렬
	List<Episode> findByNovelOrderByLikeCountDesc(Novel novel);
}
