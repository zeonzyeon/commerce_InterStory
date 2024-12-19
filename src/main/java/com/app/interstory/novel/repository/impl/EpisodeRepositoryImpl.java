package com.app.interstory.novel.repository.impl;

import static com.app.interstory.novel.domain.entity.QEpisode.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.QEpisode;
import com.app.interstory.novel.domain.entity.QNovel;
import com.app.interstory.novel.repository.EpisodeRepositoryCustom;
import com.app.interstory.user.domain.entity.QSettlement;
import com.app.interstory.user.domain.entity.QUser;
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

	@Override
	public Optional<Episode> findEpisodeWithNovelAndUserAndSettlement(Long episodeId) {

		QEpisode episode = QEpisode.episode;
		QNovel novel = QNovel.novel;
		QUser user = QUser.user;
		QSettlement settlement = QSettlement.settlement;

		return Optional.ofNullable(queryFactory
			.selectFrom(episode)
			.leftJoin(episode.novel, novel).fetchJoin()
			.leftJoin(novel.user, user).fetchJoin()
			.leftJoin(user.settlement, settlement).fetchJoin()
			.where(episode.episodeId.eq(episodeId))
			.fetchOne());
	}
}
