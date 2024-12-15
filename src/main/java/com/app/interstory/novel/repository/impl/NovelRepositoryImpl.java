package com.app.interstory.novel.repository.impl;

import static com.app.interstory.novel.domain.entity.QNovel.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.QNovel;
import com.app.interstory.novel.repository.NovelRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NovelRepositoryImpl implements NovelRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Novel> findAllWithFiltersAndSort(
		Long userId,
		String status,
		String title,
		String author,
		Boolean monetized,
		String tag,
		String sort,
		Pageable pageable
	) {
		QNovel novel = QNovel.novel;

		BooleanBuilder builder = new BooleanBuilder();

		// 필터 조건 추가
		if (userId != null) {
			builder.and(novel.user.userId.eq(userId));
		}

		// if (status != null) {
		// 	builder.and(novel.status.eq(NovelStatus.valueOf(status.toUpperCase())));
		// }

		if (title != null) {
			builder.and(novel.title.containsIgnoreCase(title));
		}

		if (author != null) {
			builder.and(novel.user.nickname.containsIgnoreCase(author));
		}

		if (monetized != null) {
			builder.and(novel.isFree.eq(!monetized));
		}

		// if (tag != null) {
		// 	builder.and(novel.tag.eq(MainTag.valueOf(tag.toUpperCase())));
		// }

		// 정렬 기준: 추천순
		OrderSpecifier<?> orderSpecifier = novel.likeCount.desc();

		List<Novel> results = queryFactory
			.selectFrom(novel)
			.where(builder)
			.orderBy(orderSpecifier)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.selectFrom(novel)
			.where(builder)
			.fetchCount();

		return new PageImpl<>(results, pageable, total);
	}

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
