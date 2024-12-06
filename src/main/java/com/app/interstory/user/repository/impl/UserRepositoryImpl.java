package com.app.interstory.user.repository.impl;

import java.util.Optional;

import com.app.interstory.user.domain.entity.QUser;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public UserRepositoryImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public Optional<User> findByIdWithSocial(Long userId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(QUser.user)
				.leftJoin(QUser.user.social).fetchJoin()
				.where(QUser.user.userId.eq(userId))
				.fetchOne()
		);
	}


}
