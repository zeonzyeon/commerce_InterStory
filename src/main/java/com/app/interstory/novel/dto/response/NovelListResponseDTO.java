package com.app.interstory.novel.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NovelListResponseDTO {
    private final List<NovelResponseDTO> novels;
    private final Integer totalPages;

    public static NovelListResponseDTO from(List<NovelResponseDTO> novels, Integer totalPage) {
        return NovelListResponseDTO.builder().novels(novels).totalPages(totalPage).build();
    }
}
