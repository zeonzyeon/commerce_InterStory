package com.app.interstory.novel.repository.impl;

import static com.app.interstory.novel.domain.entity.QNovel.*;

import java.util.Optional;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.repository.NovelRepositoryQueryDslCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NovelRepositoryQueryDslImpl implements NovelRepositoryQueryDslCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Novel> findByNovelWithUser(Long novelId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(novel)
				.leftJoin(novel.user).fetchJoin()
				.where(novel.novelId.eq(novelId))
				.fetchOne()
		);
	}
}
