package com.app.interstory.novel.domain.entity;

import com.app.interstory.user.domain.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {
	private static final Integer DEFAULT_LIKE_COUNT = 0;
	private static final Boolean DEFAULT_STATUS = true;

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
	private Integer likeCount = DEFAULT_LIKE_COUNT;

	@Builder.Default
	@Column(name = "status", nullable = false)
	private Boolean status = DEFAULT_STATUS;

    public void deleteComment() {
        this.status = !this.status;
    }

    public void setCommentLike(Integer likeCount) {
        this.likeCount = likeCount;
    }
}