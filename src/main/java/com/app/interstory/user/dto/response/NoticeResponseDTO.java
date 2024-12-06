package com.app.interstory.user.dto.response;

import com.app.interstory.user.domain.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class NoticeResponseDTO {
    private final String nickname;
    private final String title;
    private final String content;
    private final Timestamp createdAt;

    public static NoticeResponseDTO from(Notice notice) {
        return NoticeResponseDTO.builder().nickname(notice.getUser().getNickname()).title(notice.getTitle()).content(notice.getContent()).createdAt(notice.getCreatedAt()).build();
    }
}