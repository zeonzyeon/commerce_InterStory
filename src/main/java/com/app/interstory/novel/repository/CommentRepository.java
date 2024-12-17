package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
	@Query("SELECT c FROM Comment c WHERE c.episode.episodeId = :episodeId ORDER BY "
		+ "CASE WHEN :sort = '오래된순' THEN c.createdAt END ASC, " + "CASE WHEN :sort = '최신순' THEN c.createdAt END DESC, "
		+ "CASE WHEN :sort = '추천순' THEN c.likeCount END DESC")
	Page<Comment> findCommentsByEpisodeId(@Param("episodeId") Long episodeId, @Param("sort") String sort,
		Pageable pageable);

	@Query("SELECT c FROM Comment c WHERE c.episode.novel.novelId = :novelId ORDER BY "
			+ "CASE WHEN :sort = '오래된순' THEN c.createdAt END ASC, "
			+ "CASE WHEN :sort = '최신순' THEN c.createdAt END DESC, "
			+ "CASE WHEN :sort = '추천순' THEN c.likeCount END DESC")
	Page<Comment> findCommentsByNovelId(@Param("novelId") Long novelId, @Param("sort") String sort, Pageable pageable);

	@EntityGraph(attributePaths = {"episode", "episode.novel"})
	Page<Comment> findByUser_UserId(Long userId, Pageable pageable);

}
