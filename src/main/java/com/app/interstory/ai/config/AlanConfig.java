package com.app.interstory.ai.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.app.interstory.config.globalExeption.customException.AIAnalysisException;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@Getter
public class AlanConfig {

	private static final String API_URL = "https://kdt-api-function.azurewebsites.net";
	private static final String QUESTION_ENDPOINT = "/api/v1/question";
	private static final String RESET_STATE_ENDPOINT = "/api/v1/reset-state";
	private static final int MAX_DAILY_REQUESTS = 550;  // 보수적인 최대 요청량

	private final AtomicInteger requestsDayCount = new AtomicInteger(0);
	private final List<String> clientIds = Arrays.asList(
		"017a6c0d-c8c5-47c3-b1fe-07fc200a8508",
		"03278810-7d49-4869-aa82-f93622a00f7c",
		"f3d5d575-a033-4b46-bf78-e0beefbfe2a6",
		"26442bb6-c7a1-432e-8408-ad6a2b69f2ca",
		"54a5d41b-97c7-4fce-84fd-c782ff6a3573",
		"85fd5912-4117-41e1-a065-ba67b45286e8"
	);
	private volatile LocalDate lastResetDate = LocalDate.now();

	public String getClientId() {
		checkAndResetIfNewDay();
		int currentCount = requestsDayCount.incrementAndGet();

		if (currentCount > MAX_DAILY_REQUESTS) {
			log.warn("일일 최대 요청 수 초과: {}", currentCount);
			throw new AIAnalysisException("오늘의 AI 서비스는 종료되었습니다.");
		}

		return clientIds.get((currentCount - 1) % clientIds.size());
	}

	private void checkAndResetIfNewDay() {
		LocalDate today = LocalDate.now();
		if (!today.equals(lastResetDate)) {
			resetDailyCount();
			lastResetDate = today;
		}
	}

	private long getSecondsUntilMidnight() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
		return ChronoUnit.SECONDS.between(now, nextMidnight);
	}

	@Scheduled(cron = "0 0 0 * * *")  // 매일 자정
	public void resetDailyCount() {
		requestsDayCount.set(0);
	}

	public String getQuestionUrl() {
		return API_URL + QUESTION_ENDPOINT;
	}

}
