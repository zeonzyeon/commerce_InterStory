package com.app.interstory.novel.repository.impl;

import static com.app.interstory.novel.domain.entity.QEpisode.*;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.repository.EpisodeRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EpisodeRepositoryImpl implements EpisodeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Episode> getEpisodeList(Long novelId, Pageable pageable) {

		OrderSpecifier<?> orderSpecifier = pageable.getSort().getOrderFor("episodeId") != null
			&& Objects.requireNonNull(pageable.getSort().getOrderFor("episodeId")).getDirection() == Sort.Direction.ASC
			? episode.episodeId.asc()
			: episode.episodeId.desc();

		List<Episode> episodes = queryFactory
			.selectFrom(episode)
			.where(episode.novel.novelId.eq(novelId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(orderSpecifier)
			.fetch();

		Long total = queryFactory
			.select(episode.count())
			.from(episode)
			.where(episode.novel.novelId.eq(novelId))
			.fetchOne();

		return new PageImpl<>(episodes, pageable, total);
	}

}
