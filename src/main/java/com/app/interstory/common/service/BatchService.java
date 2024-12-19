package com.app.interstory.common.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.interstory.payment.service.SubscribeBatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class BatchService {

	private final SubscribeBatchService subscribeBatchService;

	// 매일 밤 12:00에 실행
	@Scheduled(cron = "0 0 0 * * *")
	public void runSubscriptionExpirationBatch() {
		log.info("Starting Subscription Expiration Batch Job");

		try {
			//구독 만료 처리
			subscribeBatchService.processExpiredSubscriptions();
			log.info("Subscription Expiration Batch Job Completed Successfully");

		} catch (Exception e) {
			log.error("Error in Subscription Expiration Batch Job", e);
		}

	}

}
