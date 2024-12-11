package com.app.interstory.user.dto.response;

import static com.app.interstory.util.Utils.*;

import com.app.interstory.user.domain.entity.Notice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponseDTO {
	private final String nickname;
	private final String title;
	private final String content;
	private final String createdAt;

	public static NoticeResponseDTO from(Notice notice) {
		return NoticeResponseDTO.builder()
			.nickname(notice.getUser().getNickname())
			.title(notice.getTitle())
			.content(notice.getContent())
			.createdAt(formatTimestamp(notice.getCreatedAt()))
			.build();
	}
}