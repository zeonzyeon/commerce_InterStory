package com.app.interstory.novel.repository.impl;

import static com.app.interstory.novel.domain.entity.QComment.*;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.app.interstory.novel.domain.entity.Comment;
import com.app.interstory.novel.repository.CommentRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Comment> findTopCommentsByEpisodeId(Long episodeId, Pageable pageable) {
		return queryFactory
			.selectFrom(comment)
			.where(comment.episode.episodeId.eq(episodeId))
			.orderBy(comment.likeCount.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}
