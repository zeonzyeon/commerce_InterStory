package com.app.interstory.novel.dto.response;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponseDto {
	private final String nickname;
	private final String profileUrl;
	private final String content;
	private final String episodeTitle;
	private final String createdAt;
	private final Integer likeCount;
	private final Boolean isLiked;
	private final Boolean status;
	private final Long commentId;
	private final Long userId;
}
