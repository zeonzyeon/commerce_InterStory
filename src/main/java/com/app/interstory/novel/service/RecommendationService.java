package com.app.interstory.novel.service;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.repository.EpisodeLikeRepository;
import com.app.interstory.novel.repository.FavoriteNovelRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.novel.repository.RecentNovelRepository;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final NovelRepository novelRepository;
    private final UserRepository userRepository;
    private final FavoriteNovelRepository favoriteNovelRepository;
    private final EpisodeLikeRepository episodeLikeRepository;
    private final RecentNovelRepository recentNovelRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String NOVEL_RECOMMENDATION_KEY = "novel:recommendation:";
    private static final int MAX_RECOMMENDATIONS = 10;
    private static final int BASE_RECOMMENDATIONS = 5;
    private static final int RANDOM_RECOMMENDATIONS = 5;
    private static final double SIMILARITY_THRESHOLD = 0.5;
    private static final int SIMILAR_USERS_LIMIT = 10;
    private static final int TAG_RECOMMENDATIONS_LIMIT = 5;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        log.info("Application is ready. Starting recommendation data update...");
        updateRecommendationData();
    }

    @Scheduled(initialDelay = 24 * 60 * 60 * 1000, fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void updateRecommendationData() {
        log.info("Starting daily recommendation data update...");
        try {
            // 기존 추천 데이터 삭제
            Set<String> allRecommendationKeys = redisTemplate.keys(NOVEL_RECOMMENDATION_KEY + "*");
            if (allRecommendationKeys != null && !allRecommendationKeys.isEmpty()) {
                redisTemplate.delete(allRecommendationKeys);
            }

            // 각 사용자별로 소설 추천 목록 계산
            List<User> users = userRepository.findAll();
            for (User user : users) {
                calculateAndStoreRecommendations(user);
            }

            log.info("Daily recommendation data update completed successfully");
        } catch (Exception e) {
            log.error("Error during recommendation data update: ", e);
        }
    }

    @Transactional(readOnly = true)
    protected void calculateAndStoreRecommendations(User user) {
        try {
            Set<Novel> recommendedNovels = new HashSet<>();

            // 1. 사용자의 직접적인 상호작용 기반 추천
            Set<Novel> interactedNovels = getUserInteractedNovels(user);
            recommendedNovels.addAll(interactedNovels);

            // 2. 비슷한 사용자들이 좋아한 소설 추가
            Set<Novel> similarUserNovels = getSimilarUserNovels(user, interactedNovels);
            recommendedNovels.addAll(similarUserNovels);

            // 3. 같은 태그의 인기 소설 추가
            Set<Novel> popularTagNovels = getPopularNovelsByPreferredTags(user, interactedNovels);
            recommendedNovels.addAll(popularTagNovels);

            // 추천 소설 5개 선택
            List<String> baseRecommendedNovelIds = recommendedNovels.stream()
                    .map(novel -> novel.getNovelId().toString())
                    .limit(BASE_RECOMMENDATIONS)
                    .toList();

            // 4. 같은 태그의 랜덤 소설
            Set<MainTag> userPreferredTags = interactedNovels.stream()
                    .map(Novel::getTag)
                    .collect(Collectors.toSet());

            List<String> randomNovelIds = getRandomNovelsByTags(userPreferredTags,
                    baseRecommendedNovelIds.stream()
                            .map(Long::parseLong)
                            .collect(Collectors.toSet()));

            // Redis에 저장
            String key = NOVEL_RECOMMENDATION_KEY + user.getUserId();
            if (!baseRecommendedNovelIds.isEmpty()) {
                redisTemplate.delete(key);
                redisTemplate.opsForList().rightPushAll(key, baseRecommendedNovelIds);
                if (!randomNovelIds.isEmpty()) {
                    redisTemplate.opsForList().rightPushAll(key, randomNovelIds);
                }
            }

            log.debug("Updated recommendations for user {}", user.getUserId());
        } catch (Exception e) {
            log.error("Error calculating recommendations for user {}: {}",
                    user.getUserId(), e.getMessage());
        }
    }

    private Set<Novel> getUserInteractedNovels(User user) {
        Set<Novel> interactedNovels = new HashSet<>();

        // 즐겨찾기한 소설 추가
        favoriteNovelRepository.findByUser(user)
                .forEach(favorite -> interactedNovels.add(favorite.getNovel()));

        // 좋아요한 에피소드의 소설 추가
        episodeLikeRepository.findByUser(user)
                .forEach(like -> interactedNovels.add(like.getEpisode().getNovel()));

        // 최근 읽은 소설 추가
        recentNovelRepository.findByUser(user)
                .forEach(recent -> interactedNovels.add(recent.getNovel()));

        return interactedNovels;
    }

    private Set<Novel> getSimilarUserNovels(User user, Set<Novel> interactedNovels) {
        // 1. 현재 사용자와 비슷한 취향을 가진 사용자들 찾기
        List<User> similarUsers = userRepository.findAll().stream()
                .filter(otherUser -> !otherUser.equals(user))
                .filter(otherUser -> calculateUserSimilarity(user, otherUser) > SIMILARITY_THRESHOLD)
                .limit(SIMILAR_USERS_LIMIT)
                .toList();

        // 2. 비슷한 사용자들이 좋아한 소설들 수집
        return similarUsers.stream()
                .flatMap(similarUser -> getUserInteractedNovels(similarUser).stream())
                .filter(novel -> !interactedNovels.contains(novel))
                .collect(Collectors.toSet());
    }

    private double calculateUserSimilarity(User user1, User user2) {
        Set<Novel> user1Novels = getUserInteractedNovels(user1);
        Set<Novel> user2Novels = getUserInteractedNovels(user2);

        // Jaccard 유사도 계산
        Set<Novel> intersection = new HashSet<>(user1Novels);
        intersection.retainAll(user2Novels);

        Set<Novel> union = new HashSet<>(user1Novels);
        union.addAll(user2Novels);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    private Set<Novel> getPopularNovelsByPreferredTags(User user, Set<Novel> interactedNovels) {
        // 사용자가 선호하는 태그 파악
        Map<MainTag, Long> tagPreferences = interactedNovels.stream()
                .map(Novel::getTag)
                .collect(Collectors.groupingBy(
                        tag -> tag,
                        Collectors.counting()
                ));

        // 가장 선호하는 상위 태그들 선택
        Set<MainTag> preferredTags = tagPreferences.entrySet().stream()
                .sorted(Map.Entry.<MainTag, Long>comparingByValue().reversed())
                .limit(3) // 상위 3개 태그 선택
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // 선호 태그의 인기 소설 추천
        return novelRepository.findAll().stream()
                .filter(novel -> preferredTags.contains(novel.getTag()))
                .filter(novel -> !interactedNovels.contains(novel))
                .sorted(Comparator.comparing(Novel::getLikeCount).reversed())
                .limit(TAG_RECOMMENDATIONS_LIMIT)
                .collect(Collectors.toSet());
    }

    private List<String> getRandomNovelsByTags(Set<MainTag> tags, Set<Long> excludeNovelIds) {
        try {
            // 해당 태그들의 소설 중에서 랜덤으로 선택
            return novelRepository.findAll().stream()
                    .filter(novel -> tags.contains(novel.getTag()))
                    .filter(novel -> !excludeNovelIds.contains(novel.getNovelId()))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            collected -> {
                                Collections.shuffle(collected); // 랜덤으로 섞기
                                return collected.stream()
                                        .limit(RANDOM_RECOMMENDATIONS)
                                        .map(novel -> novel.getNovelId().toString())
                                        .collect(Collectors.toList());
                            }
                    ));
        } catch (Exception e) {
            log.error("Error getting random novels: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<NovelResponseDTO> getRecommendedNovels(CustomUserDetails userDetails) {
        try {
            User user = userRepository.findById(userDetails.getUser().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            String key = NOVEL_RECOMMENDATION_KEY + user.getUserId();
            List<String> recommendedNovelIds = redisTemplate.opsForList()
                    .range(key, 0, MAX_RECOMMENDATIONS - 1);

            if (recommendedNovelIds == null || recommendedNovelIds.isEmpty()) {
                // 추천 데이터가 없는 경우 실시간으로 계산
                calculateAndStoreRecommendations(user);
                recommendedNovelIds = redisTemplate.opsForList()
                        .range(key, 0, MAX_RECOMMENDATIONS - 1);

                if (recommendedNovelIds == null || recommendedNovelIds.isEmpty()) {
                    return Collections.emptyList();
                }
            }

            return recommendedNovelIds.stream()
                    .map(id -> novelRepository.findById(Long.parseLong(id)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(NovelResponseDTO::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting recommendations for user {}: {}",
                    userDetails.getUsername(), e.getMessage());
            return Collections.emptyList();
        }
    }
}