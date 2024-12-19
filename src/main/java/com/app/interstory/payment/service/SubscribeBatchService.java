package com.app.interstory.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.payment.domain.entity.Subscribe;
import com.app.interstory.payment.domain.repository.SubscribeRepository;
import com.app.interstory.user.domain.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SubscribeBatchService {

	private final SubscribeRepository subscribeRepository;

	@Transactional
	public void processExpiredSubscriptions() {

		// 만료된 구독 찾기
		List<Subscribe> expiredSubscriptions = subscribeRepository.findSubscriptionsToExpireToday();

		if (expiredSubscriptions.isEmpty()) {
			log.info("No expired subscriptions to process.");
			return;
		}

		// 각 만료된 구독 처리
		for (Subscribe subscribe : expiredSubscriptions) {
			// 구독 상태 업데이트
			subscribe.updateIsContinue(false);
			//유저 구독 상태 업데이트
			User user = subscribe.getUser();
			user.updateSubscriptionStatus(false);

			log.info("Subscription expired for user: {}", user.getUserId());
		}
	}

}
