package com.app.interstory.novel.repository.impl;

import static com.app.interstory.novel.domain.entity.QComment.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

	@Override
	public Page<Comment> findByUserWithNovelAndEpisode(Long userId, Pageable pageable) {
		Long total = queryFactory
			.select(comment.count())
			.from(comment)
			.where(comment.user.userId.eq(userId))
			.fetchOne();

		// null 체크 및 기본값 설정
		total = total != null ? total : 0L;

		// 페이징된 데이터 조회
		List<Comment> comments = queryFactory
			.selectFrom(comment)
			.leftJoin(comment.user).fetchJoin()
			.leftJoin(comment.episode).fetchJoin()
			.leftJoin(comment.episode.novel).fetchJoin()
			.where(
				comment.user.userId.eq(userId),
				comment.status.isTrue()
			)
			.orderBy(comment.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(comments, pageable, total);
	}
}
