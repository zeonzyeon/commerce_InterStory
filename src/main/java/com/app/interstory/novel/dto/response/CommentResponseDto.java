package com.app.interstory.novel.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;


@Getter
@Setter
@Builder
public class CommentResponseDto {
    private final String nickname;
    private final String profileUrl;
    private final String content;
    private final Timestamp createdAt;
    private final Integer likeCount;
    private final Boolean isLiked;
    private final Boolean status;
}
