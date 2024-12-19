package com.app.interstory.novel.dto.response;

import com.app.interstory.novel.domain.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
	private String nickname;
	private String profileUrl;
	private String content;
	private String episodeTitle;
	private String createdAt;
	private Integer likeCount;
	private Boolean isLiked;
	private Boolean status;
    private Long commentId;
    private Long userId;

	public static CommentResponseDto fromComment(Comment comment) {
		return CommentResponseDto.builder()
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt().toString())
			.likeCount(comment.getLikeCount())
			.status(comment.getStatus())
			.build();
	}

}
