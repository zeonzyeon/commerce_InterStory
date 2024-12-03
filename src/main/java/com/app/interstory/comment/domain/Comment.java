package com.app.interstory.comment.domain;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;

import com.app.interstory.user.domain.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "episode_id", nullable = false)
	private Episode episode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "created_at", nullable = false)
	@CreatedDate
	private Timestamp createdAt;

	@Builder.Default
	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0;

	@Builder.Default
	@Column(name = "status", nullable = false)
	private Boolean status = true;
}