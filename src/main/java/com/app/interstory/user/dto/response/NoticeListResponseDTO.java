package com.app.interstory.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NoticeListResponseDTO {
    private final List<NoticeResponseDTO> notices;
    private final Integer totalPage;

    public static NoticeListResponseDTO from(List<NoticeResponseDTO> notices, Integer totalPage) {
        return NoticeListResponseDTO.builder().notices(notices).totalPage(totalPage).build();
    }
}
