package com.app.interstory.novel.domain.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.user.domain.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "novel")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Novel {
	private static final NovelStatus DEFAULT_STATUS = NovelStatus.DRAFT;
	private static final Integer DEFAULT_FAVORITE_COUNT = 0;
	private static final Integer DEFAULT_LIKE_COUNT = 0;
	private static final Boolean DEFAULT_IS_FREE = false;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "novel_id")
	private Long novelId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "novel", cascade = CascadeType.ALL)
	private List<Episode> episodes = new ArrayList<>();

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT", nullable = false)
	private String description;

	@Column(name = "plan", columnDefinition = "TEXT", nullable = false)
	private String plan;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private NovelStatus status = DEFAULT_STATUS;

	@Column(name = "thumbnail_renamed_filename")
	private String thumbnailRenamedFilename;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@Builder.Default
	@Column(name = "favorite_count", nullable = false)
	private Integer favoriteCount = DEFAULT_FAVORITE_COUNT;

	@Builder.Default
	@Column(name = "like_count", nullable = false)
	private Integer likeCount = DEFAULT_LIKE_COUNT;

	@Builder.Default
	@Column(name = "is_free", nullable = false)
	private Boolean isFree = DEFAULT_IS_FREE;

	@Column(name = "tag", nullable = false)
	@Enumerated(EnumType.STRING)
	private MainTag tag;

	@Column(name = "published_at", nullable = false)
	@CreatedDate
	private Timestamp publishedAt;

	@Column(name = "episode_updated_at")
	private Timestamp episodeUpdatedAt;

	public void update(
		String title,
		String description,
		String thumbnailRenamedFilename,
		String thumbnailUrl,
		MainTag tag,
		NovelStatus status,
		Boolean isFree
	) {
		this.title = title;
		this.description = description;
		this.thumbnailRenamedFilename = thumbnailRenamedFilename;
		this.thumbnailUrl = thumbnailUrl;
		this.tag = tag;
		this.status = status;
		this.isFree = isFree;
	}

	public void updateStatus(NovelStatus status) {
		this.status = status;
	}

	public void markAsDeleted() {
		this.status = NovelStatus.DELETED;
	}

	public void updateFavoriteCount(Integer favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public void updateThumbnail(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
		this.thumbnailRenamedFilename = thumbnailUrl.substring(thumbnailUrl.lastIndexOf("/") + 1);
	}
}
