package com.app.interstory.novel.domain.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.app.interstory.novel.dto.request.EpisodeRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "episode")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Episode {
	private static final Integer DEFAULT_VIEW_COUNT = 0;
	private static final Integer DEFAULT_LIKE_COUNT = 0;
	private static final Integer DEFAULT_COMMENT_COUNT = 0;
	private static final Integer DEFAULT_EP_NUM = 0;
	private static final Boolean DEFAULT_STATUS = true;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "episode_id")
	private Long episodeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "novel_id", nullable = false)
	private Novel novel;
	
	@Column(name = "title", nullable = false)
	private String title;

	@Builder.Default
	@Column(name = "view_count", nullable = false)
	private Integer viewCount = DEFAULT_VIEW_COUNT;

	@Column(name = "published_at", nullable = false, updatable = false)
	@CreatedDate
	private Timestamp publishedAt;

	@Column(name = "thumbnail_renamed_filename")
	private String thumbnailRenamedFilename;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Builder.Default
	@Column(name = "like_count", nullable = false)
	private Integer likeCount = DEFAULT_LIKE_COUNT;

	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	private String content;

	@Builder.Default
	@Column(name = "comment_count", nullable = false)
	private Integer commentCount = DEFAULT_COMMENT_COUNT;

	@Builder.Default
	@Column(name = "status", nullable = false)
	private Boolean status = DEFAULT_STATUS;

	@Builder.Default
	@Column(name = "episode_number")
	private Integer episodeNumber = DEFAULT_EP_NUM;

	public void updateEpisode(EpisodeRequestDTO requestDTO) {
		if (requestDTO.getTitle() != null) {
			this.title = requestDTO.getTitle();
		}
		if (requestDTO.getThumbnailRenamedFilename() != null) {
			this.thumbnailRenamedFilename = requestDTO.getThumbnailRenamedFilename();
		}
		if (requestDTO.getThumbnailUrl() != null) {
			this.thumbnailUrl = requestDTO.getThumbnailUrl();
		}
		if (requestDTO.getContent() != null) {
			this.content = requestDTO.getContent();
		}
		if (requestDTO.getStatus() != null) {
			this.status = requestDTO.getStatus();
		}
	}

	public void markAsDeleted() {
		this.status = false;
	}

	public void incrementLikeCount() {
		this.likeCount++;
	}

	// 추천 감소
	public void decrementLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}
	}
}
