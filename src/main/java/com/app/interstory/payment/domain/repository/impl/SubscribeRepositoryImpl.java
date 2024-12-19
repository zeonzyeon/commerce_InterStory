package com.app.interstory.payment.domain.repository.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.app.interstory.payment.domain.entity.QSubscribe;
import com.app.interstory.payment.domain.entity.Subscribe;
import com.app.interstory.payment.domain.repository.SubscribeRepositoryCustom;
import com.app.interstory.user.domain.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Subscribe> findSubscriptionsToExpireToday() {
		QSubscribe subscribe = QSubscribe.subscribe;
		QUser user = QUser.user;

		LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

		return queryFactory
			.selectFrom(subscribe)
			.join(subscribe.user, user)
			.where(
				subscribe.endAt.between(
					Timestamp.valueOf(startOfDay),
					Timestamp.valueOf(endOfDay)
				),
				subscribe.isContinue.isTrue()
			)
			.fetch();
	}
}
