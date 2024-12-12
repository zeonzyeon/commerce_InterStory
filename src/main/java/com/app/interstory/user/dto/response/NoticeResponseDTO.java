package com.app.interstory.user.dto.response;

import com.app.interstory.user.domain.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import static com.app.interstory.util.Utils.formatTimestamp;

@Getter
@Builder
public class NoticeResponseDTO {
	private final Long noticeId;
	private final String nickname;
	private final String title;
	private final String content;
	private final String createdAt;

	public static NoticeResponseDTO from(Notice notice) {
		return NoticeResponseDTO.builder()
				.noticeId(notice.getNoticeId())
				.nickname(notice.getUser().getNickname())
				.title(notice.getTitle())
				.content(notice.getContent())
				.createdAt(formatTimestamp(notice.getCreatedAt()))
				.build();
	}
}