package com.app.interstory.ai.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.app.interstory.ai.dto.ReactionAnalysisDto;
import com.app.interstory.config.globalExeption.customException.AIAnalysisException;
import com.app.interstory.novel.domain.entity.Comment;
import com.app.interstory.novel.repository.CommentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReactionAnalysisService {
	private static final String CACHE_KEY_PREFIX = "reaction:analysis:"; // 캐상 카 구조 + {epId}
	private static final long CACHE_DURATION_SECONDS = 24 * 60 * 60 * 2; //2일간 저장
	private static final int MAX_COMMENTS = 20;
	private static final int Min_COMMENTS = 5;
	private static final int MAX_CONTENT_LENGTH = 850;
	private final CommentRepository commentRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final AiService aiService;
	private final ObjectMapper objectMapper;

	//독자 반응 분석
	public ReactionAnalysisDto getEpisodeReaction(Long episodeId) throws JsonProcessingException {

		String cacheKey = CACHE_KEY_PREFIX + episodeId;
		String cachedAnalysis = redisTemplate.opsForValue().get(cacheKey);

		if (cachedAnalysis != null) {
			return objectMapper.readValue(cachedAnalysis, ReactionAnalysisDto.class);
		}

		String prompt = getTopCommentsPrompt(episodeId);

		//앨런 질문 요청
		String response = aiService.analyzeText(prompt);

		ReactionAnalysisDto reactionAnalysisDto = createReactionAnalysisDto(response, episodeId);

		//redis 저장
		cacheAnalysis(cacheKey, reactionAnalysisDto);

		return reactionAnalysisDto;
	}

	//redis 저장
	private void cacheAnalysis(String cacheKey, ReactionAnalysisDto analysisDto) throws JsonProcessingException {
		redisTemplate.opsForValue().set(
			cacheKey,
			objectMapper.writeValueAsString(analysisDto),
			CACHE_DURATION_SECONDS,
			TimeUnit.SECONDS
		);
	}

	//좋아요 순 20개 댓글 850자 내외로 취합한 prompt + 댓글 수 5개 이하면 에러
	public String getTopCommentsPrompt(Long episodeId) {

		List<Comment> topComments = commentRepository.findTopCommentsByEpisodeId(
			episodeId,
			PageRequest.of(0, MAX_COMMENTS, Sort.by("likeCount").descending())
		);

		if (topComments == null || topComments.isEmpty() || topComments.size() < Min_COMMENTS) {

			throw new AIAnalysisException("댓글 수가 5개 이하입니다. 댓글 갯수 :"
				+ (topComments != null ? topComments.size() : 0));
		}

		String comment = topComments.stream()
			.map(Comment::getContent)
			.reduce(new StringBuilder(), (builder, content) -> {
				// 현재까지의 총 길이 + 새로운 댓글 길이가 1000자 미만일 때만 추가
				if (builder.length() + content.length() + 1 <= MAX_CONTENT_LENGTH) {
					if (!builder.isEmpty()) {
						builder.append("\n");
					}
					builder.append(content);
				}
				return builder;
			}, StringBuilder::append)
			.toString();

		return String.format(
			"""
				다음 댓글의 반응을 분석하고 요약해 줘:
				댓글: %s
				
				다음 형식으로 응답해 줘:
				- 긍정 점수 (0~100):
				- 부정 점수 (0~100):
				- 핵심 키워드:
				- 반응 요약 :
				""",
			comment
		);
	}

	//convert
	private ReactionAnalysisDto createReactionAnalysisDto(String response, Long episodeId) {
		return ReactionAnalysisDto.builder()
			.episodeId(episodeId)
			.aiAnalysis(response)
			.analyzedAt(LocalDateTime.now())
			.build();
	}
}
