package com.app.interstory.novel.service;

import com.app.interstory.common.service.S3Service;
import com.app.interstory.config.FilePathConfig;
import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.FavoriteNovel;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.Tag;
import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.domain.enumtypes.SortType;
import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.dto.request.NovelSortRequestDTO;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.dto.response.NovelListResponseDTO;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.FavoriteNovelRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.novel.repository.TagRepository;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.enumtypes.NovelSortType;
import com.app.interstory.user.domain.enumtypes.Roles;
import com.app.interstory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NovelService {
    private final NovelRepository novelRepository;
    private final UserRepository userRepository;
    private final EpisodeRepository episodeRepository;
    private final TagRepository tagRepository;
    @Qualifier("redisObjectTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final FavoriteNovelRepository favoriteNovelRepository;
    private final S3Service s3Service;
    private final FilePathConfig filePathConfig;

    // 소설 작성
    @Transactional
    public Long writeNovel(NovelRequestDTO novelRequestDTO, MultipartFile file, CustomUserDetails userDetails) {

        String thumbnailUrl = file != null ? uploadThumbnail(file) : filePathConfig.getDefaultThumbnailPath();
        String renamedFileName = thumbnailUrl.substring(thumbnailUrl.lastIndexOf("/") + 1);

        String filePath = filePathConfig.getThumbnailPath() + renamedFileName;

        Novel novel = Novel.builder()
                .user(userRepository.findById(userDetails.getUser().getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .title(novelRequestDTO.getTitle())
                .description(novelRequestDTO.getDescription())
                .plan(novelRequestDTO.getPlan())
                .thumbnailUrl(filePath)
                .thumbnailRenamedFilename(renamedFileName)
                .status(NovelStatus.DRAFT)
                .tag(novelRequestDTO.getTag())
                .isFree(novelRequestDTO.getIsFree())
                .build();

        novelRepository.save(novel);

        List<String> customTags = novelRequestDTO.getCustomTag();
        if (customTags != null) {
            for (String tagName : customTags) {
                Tag tag = new Tag(novel, tagName);
                tagRepository.save(tag);
            }
        }

        return novel.getNovelId();
    }

    // 소설 수정
    @Transactional
    public void updateNovel(Long novelId, NovelRequestDTO novelRequestDTO, MultipartFile file, CustomUserDetails userDetails) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Novel not found"));

        if (!novel.getUser().getUserId().equals(userDetails.getUser().getUserId()) && userDetails.getUser().getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized user");
        }

        String thumbnailUrl = updateThumbnailImage(novel, file);
        String renamedFileName = thumbnailUrl.substring(thumbnailUrl.lastIndexOf("/") + 1);
        String filePath = filePathConfig.getThumbnailPath() + renamedFileName;

        tagRepository.deleteByNovel(novel);

        List<String> customTags = novelRequestDTO.getCustomTag();
        if (customTags != null) {
            for (String tagName : customTags) {
                Tag tag = new Tag(novel, tagName);
                tagRepository.save(tag);
            }
        }

        novel.update(
                novelRequestDTO.getTitle(),
                novelRequestDTO.getDescription(),
                renamedFileName,
                filePath,
                novelRequestDTO.getTag(),
                novelRequestDTO.getStatus(),
                novelRequestDTO.getIsFree()
        );
    }

    // 소설 상세 조회
    public NovelDetailResponseDTO readNovel(Long novelId, CustomUserDetails userDetails) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Novel not found"));

        return new NovelDetailResponseDTO(
                novel.getNovelId(),
                novel.getUser().getUserId(),
                novel.getTitle(),
                novel.getDescription(),
                novel.getPlan(),
                novel.getThumbnailUrl(),
                novel.getStatus(),
                novel.getTag(),
                tagRepository.findByNovel(novel),
                novel.getIsFree(),
                novel.getFavoriteCount(),
                novel.getLikeCount(),
                episodeRepository.countByNovel(novel),
                episodeRepository.findByNovel(novel).stream()
                        .mapToInt(Episode::getCommentCount)
                        .sum(),
                favoriteNovelRepository.existsByUser_UserId(userDetails.getUser().getUserId())
        );
    }

    // 소설 목록 조회
    public NovelListResponseDTO getNovelList(
            NovelStatus status,
            String title,
            String author,
            Boolean monetized,
            MainTag tag,
            SortType sort,
            Integer page
    ) {
        final int getItemCount = 10;

        Pageable pageable = PageRequest.of(page - 1, getItemCount);

        Page<Novel> novelPages = novelRepository.findByFilterAndSort(
                status, title, author, monetized, tag, sort.getDescription(), pageable
        );

        if (page > novelPages.getTotalPages()) {
            throw new RuntimeException("유효하지 않은 페이지입니다.");
        }

        List<NovelResponseDTO> novels = novelPages.stream()
                .map(novel -> {
                    List<String> customTags = tagRepository.findByNovel(novel).stream()
                            .map(Tag::getTag)
                            .toList();
                    return NovelResponseDTO.from(novel, customTags);
                })
                .toList();

        return NovelListResponseDTO.from(novels, novelPages.getTotalPages());
    }

    // 소설 삭제
    @Transactional
    public void deleteNovel(Long novelId, CustomUserDetails userDetails) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Novel not found with id: " + novelId));

        if (!novel.getUser().getUserId().equals(userDetails.getUser().getUserId()) && userDetails.getUser().getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized user");
        }

        novel.markAsDeleted();
    }

    // 관심작품 등록
    @Transactional
    public String likeNovel(Long userId, Long novelId) {
        String afterLikeMessage;

        // 에피소드 조회
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Novel not found"));

        if (favoriteNovelRepository.existsByUser_UserId(userId)) {
            favoriteNovelRepository.deleteByUser_UserId(userId);
            novel.updateFavoriteCount(novel.getFavoriteCount() - 1);
            afterLikeMessage = "관심 작품에서 제거되었습니다.";
        } else {
            FavoriteNovel favoriteNovel = FavoriteNovel.builder()
                    .user(userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found")))
                    .novel(novel)
                    .build();
            favoriteNovelRepository.save(favoriteNovel);
            novel.updateFavoriteCount(novel.getFavoriteCount() + 1);
            afterLikeMessage = "관심 작품으로 등록했습니다.";
        }

        return afterLikeMessage;
    }

    // 태그별 인기 작품 캐싱 (TTL: 3시간)
    public List<NovelResponseDTO> getPopularNovelsByTag(NovelSortRequestDTO request) {

        String cacheKey = "popular:novels:" + request.getMainTag() + request.getType();
        List<NovelResponseDTO> cached = (List<NovelResponseDTO>) redisTemplate.opsForValue().get(cacheKey);

        if (cached == null) {
            log.info("**********cache refresh check*******************");
            cached = novelRepository.findPopularNovelsByTag(request).stream()
                    .map(NovelResponseDTO::from)
                    .toList();

            redisTemplate.opsForValue().set(cacheKey, cached, 3, TimeUnit.HOURS);
        }

        return cached;
    }

    //인기순 최신순 이름순 캐싱 - 3시간
    public List<NovelResponseDTO> getOrderedNovel(NovelSortType novelSortType) {

        String cacheKey = "popular:novels:cache:" + novelSortType;
        log.info("cacheKey*****{}", cacheKey);
        List<NovelResponseDTO> cached = (List<NovelResponseDTO>) redisTemplate.opsForValue().get(cacheKey);

        if (cached == null) {
            log.info("**********cache refresh check*******************");

            cached = novelRepository.findNovelByOrder(novelSortType).stream()
                    .map(NovelResponseDTO::from)
                    .toList();

            redisTemplate.opsForValue().set(cacheKey, cached, 3, TimeUnit.HOURS);
        }

        return cached;
    }

    // 썸네일 이미지 업데이트
    @Transactional
    public String updateThumbnailImage(Novel novel, MultipartFile file) {

        // 기존 썸네일 삭제
        deleteExistingThumbnail(novel);

        // 새 썸네일 업로드
        String thumbnailUrl = uploadThumbnail(file);
        novel.updateThumbnail(thumbnailUrl);

        return thumbnailUrl;
    }

    private String uploadThumbnail(MultipartFile file) {
        String dirPath = filePathConfig.getThumbnail();
        return s3Service.uploadFile(file, dirPath);
    }

    private void deleteExistingThumbnail(Novel novel) {
        if (!novel.getThumbnailRenamedFilename().equals("novel.png")) {
            s3Service.deleteFile(novel.getThumbnailUrl(), filePathConfig.getThumbnail());
        }
    }
}
