package com.app.interstory.novel.dto.response;

import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EpisodeDetailResponseDto {

    private Long episodeId;
    private String title;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private Integer episodeNumber;
    private Novel novel;
    private String thumbnailUrl;

    public static EpisodeDetailResponseDto from(Episode episode) {
        return EpisodeDetailResponseDto.builder()
                .episodeId(episode.getEpisodeId())
                .title(episode.getTitle())
                .content(episode.getContent())
                .episodeNumber(episode.getEpisodeNumber())
                .commentCount(episode.getCommentCount())
                .likeCount(episode.getLikeCount())
                .viewCount(episode.getViewCount())
                .novel(episode.getNovel())
                .thumbnailUrl(episode.getThumbnailUrl())
                .build();
    }
}
