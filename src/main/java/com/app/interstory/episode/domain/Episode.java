package com.app.interstory.episode.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "episode")
public class Episode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "episode_id")
	private Long episodeId;

	@Column(name = "novel_id", nullable = false)
	private Long novelId;

	@Column(name = "title", columnDefinition = "VARCHAR(63)")
	private String title;

	@Column(name = "view_count")
	private Integer viewCount;

	@CreatedDate
	@Column(name = "published_at", updatable = false)
	private Timestamp publishedAt;

	@Column(name = "thumbnail_renamed_filename", columnDefinition = "VARCHAR(255)")
	private String thumbnailRenamedFilename;

	@Column(name = "thumbnail_url", columnDefinition = "VARCHAR(255)")
	private String thumbnailUrl;

	@Column(name = "like_count")
	private Integer likeCount;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "status", columnDefinition = "TINYINT(1)")
	private String status;
}