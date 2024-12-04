package com.app.interstory.novel.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CommentListResponseDto {
    private final List<CommentResponseDto> comments;
    private final Integer totalPage;

    public static CommentListResponseDto from(List<CommentResponseDto> comments, Integer totalPage) {
        return CommentListResponseDto.builder()
                .comments(comments)
                .totalPage(totalPage)
                .build();
    }
}