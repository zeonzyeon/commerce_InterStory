package com.app.interstory.user.dto.response;

import java.sql.Timestamp;

import com.app.interstory.novel.domain.entity.Comment;
import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.user.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentResponseDTO {
	private Long commentId;
	private String novelTitle;
	private Integer episodeNumber;
	private String content;
	private String episodeTitle;
	private Timestamp createdAt;
	private Integer likeCount;

	public static MyCommentResponseDTO createMyCommentResponseDTO(Comment comment) {
		Episode episode = comment.getEpisode();
		Novel novel = episode.getNovel();
		User user = comment.getUser();

		return MyCommentResponseDTO.builder()
			.commentId(comment.getCommentId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.episodeNumber(episode.getEpisodeNumber())
			.episodeTitle(episode.getTitle())
			.episodeNumber(episode.getEpisodeNumber())
			.novelTitle(novel.getTitle())
			.build();
	}
}
