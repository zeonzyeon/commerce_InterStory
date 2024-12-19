package com.app.interstory.payment.domain.repository;

import java.util.List;

import com.app.interstory.payment.domain.entity.Subscribe;

public interface SubscribeRepositoryCustom {
	List<Subscribe> findSubscriptionsToExpireToday();
}
