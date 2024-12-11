package com.app.interstory.user.dto.response;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyCommentResponseDTO {
	String novelTitle;
	Long episodeNumber;
	String content;
	Timestamp createdAt;
	Integer likeCount;
}
