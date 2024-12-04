package com.app.interstory.novel.domain;

import com.app.interstory.user.domain.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "novel")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Novel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "novel_id")
	private Long novelId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT", nullable = false)
	private String description;

	@Column(name = "plan", columnDefinition = "TEXT", nullable = false)
	private String plan;

	@Builder.Default
	@Column(name = "status", nullable = false)
	private NovelStatus status = NovelStatus.DRAFT;

	@Column(name = "thumbnail_renamed_filename")
	private String thumbnailRenamedFilename;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Builder.Default
	@Column(name = "favorite_count", nullable = false)
	private Integer favoriteCount = 0;

	@Builder.Default
	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0;

	@Builder.Default
	@Column(name = "is_free", nullable = false)
	private Boolean isFree = false;

	@Column(name = "tag", nullable = false)
	private MainTag tag;
}
