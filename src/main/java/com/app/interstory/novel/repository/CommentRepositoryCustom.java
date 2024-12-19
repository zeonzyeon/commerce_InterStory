package com.app.interstory.novel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.interstory.novel.domain.entity.Comment;

public interface CommentRepositoryCustom {

	List<Comment> findTopCommentsByEpisodeId(Long episodeId, Pageable pageable);

	Page<Comment> findByUserWithNovelAndEpisode(Long userId, Pageable pageable);

}
