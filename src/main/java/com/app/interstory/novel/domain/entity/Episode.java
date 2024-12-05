package com.app.interstory.novel.domain.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
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
public class Episode {
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
	private Integer viewCount = 0;

	@Column(name = "published_at", nullable = false, updatable = false)
	@CreatedDate
	private Timestamp publishedAt;

	@Column(name = "thumbnail_renamed_filename")
	private String thumbnailRenamedFilename;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Builder.Default
	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0;

	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	private String content;

	@Builder.Default
	@Column(name = "status", nullable = false)
	private Boolean status = true;
}
