package com.app.interstory.novel.service;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.domain.enumtypes.Sort;
import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.dto.response.EpisodeResponseDTO;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NovelService {
    private final NovelRepository novelRepository;
    private final UserRepository userRepository;
    private final EpisodeRepository episodeRepository;

    // 소설 작성
    public Long writeNovel(NovelRequestDTO novelRequestDTO, Long userId) {
        Novel novel = Novel.builder()
                .user(userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .title(novelRequestDTO.getTitle())
                .description(novelRequestDTO.getDescription())
                .plan(novelRequestDTO.getPlan())
                .status(NovelStatus.DRAFT)
                .thumbnailRenamedFilename(novelRequestDTO.getThumbnailRenamedFilename())
                .thumbnailUrl(novelRequestDTO.getThumbnailUrl())
                .tag(novelRequestDTO.getTag())
                .isFree(novelRequestDTO.getIsFree())
                .build();

        novelRepository.save(novel);
        return novel.getNovelId();
    }

    // 소설 수정
    @Transactional
    public void updateNovel(Long novelId, NovelRequestDTO novelRequestDTO, Long userId) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Novel not found"));

        if (!novel.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized user");
        }

        novel.update(
                novelRequestDTO.getTitle(),
                novelRequestDTO.getDescription(),
                novelRequestDTO.getPlan(),
                novelRequestDTO.getThumbnailRenamedFilename(),
                novelRequestDTO.getThumbnailUrl(),
                novelRequestDTO.getTag(),
                novelRequestDTO.getStatus(),
                novelRequestDTO.getIsFree()
        );
    }

    // 소설 상세 조회
    public NovelDetailResponseDTO readNovel(Long novelId, Sort sort, Pageable pageable) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Novel not found"));

        Page<Episode> episodes;
        switch (sort) {
            case RECOMMENDATION:
                episodes = episodeRepository.findEpisodesByNovelIdOrderByLikeCount(novelId, pageable);
                break;
            case OLD_TO_NEW:
                episodes = episodeRepository.findEpisodesByNovelIdOrderByPublishedAtAsc(novelId, pageable);
                break;
            default:
                episodes = episodeRepository.findEpisodesByNovelIdOrderByPublishedAtDesc(novelId, pageable);
                break;
        }

        List<EpisodeResponseDTO> episodeDTOs = episodes.getContent().stream()
                .map(episode -> EpisodeResponseDTO.builder()
                        .episodeId(episode.getEpisodeId())
                        .novelId(novelId)
                        .title(episode.getTitle())
                        .viewCount(episode.getViewCount())
                        .publishedAt(episode.getPublishedAt())
                        .thumbnailUrl(episode.getThumbnailUrl())
                        .likeCount(episode.getLikeCount())
                        .content(null)
                        .status(episode.getStatus())
                        .build()
                )
                .collect(Collectors.toList());

        return new NovelDetailResponseDTO(
                novel.getNovelId(),
                novel.getTitle(),
                novel.getDescription(),
                novel.getThumbnailUrl(),
                novel.getStatus(),
                novel.getTag(),
                episodeDTOs,
                episodes.getTotalPages()
        );
    }

    // 소설 목록 조회
    public Page<NovelResponseDTO> getNovelList(
            NovelStatus status,
            String title,
            String author,
            Boolean monetized,
            MainTag tag,
            Sort sort,
            Pageable pageable
    ) {
        Page<Novel> novels = novelRepository.findAllWithDynamicSort(
                status, title, author, monetized, tag, sort.name(), pageable
        );

        return novels.map(novel -> NovelResponseDTO.builder()
                .novelId(novel.getNovelId())
                .title(novel.getTitle())
                .description(novel.getDescription())
                .thumbnailUrl(novel.getThumbnailUrl())
                .likeCount(novel.getLikeCount())
                .status(novel.getStatus())
                .tag(novel.getTag())
                .createdAt(novel.getPublishedAt())
                .build());
    }

    // 소설 삭제
    @Transactional
    public void deleteNovel(Long novelId, Long userId) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Novel not found with id: " + novelId));

        if (!novel.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized user");
        }

        if (novel.getStatus() == NovelStatus.DELETED) {
            throw new RuntimeException("Novel is already deleted.");
        }

        novel.markAsDeleted();
        novelRepository.save(novel);
    }
}
