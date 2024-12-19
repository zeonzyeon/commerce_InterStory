package com.app.interstory.ai.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.app.interstory.ai.config.AlanConfig;
import com.app.interstory.config.globalExeption.customException.AIAnalysisException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AiService {

	private final RestTemplate restTemplate;
	private final AlanConfig alanConfig;

	//독자 반응 분석 전송
	public String analyzeText(String prompt) {

		String clientId = alanConfig.getClientId();
		String url = alanConfig.getQuestionUrl();

		// log.info("prompt: **********:{}", prompt);
		// log.info("clientId: **********:{}", clientId);

		String requestUrl = UriComponentsBuilder
			.fromUriString(url)
			.queryParam("content", prompt)
			.queryParam("client_id", clientId)
			.toUriString();

		try {
			ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				log.error("AI 서버 에러 응답: {}", response.getStatusCode());
				throw new AIAnalysisException("AI 서버 응답 오류");
			}

			JsonNode root = new ObjectMapper().readTree(response.getBody());
			// log.info("ai 반응 : **********:{}", root.path("content").asText());
			return root.path("content").asText();

		} catch (RestClientException e) {
			log.error("AI 서버 통신 오류", e);
			throw new AIAnalysisException("AI 서버 통신 실패");
		} catch (JsonProcessingException e) {
			log.error("AI 응답 파싱 오류", e);
			throw new AIAnalysisException("AI 응답 파싱 실패");
		}

	}
}
