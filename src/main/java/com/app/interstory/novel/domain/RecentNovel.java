package com.app.interstory.novel.domain;

import com.app.interstory.user.domain.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recent_novel")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RecentNovel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recent_novel_id")
	private Long recentNovelId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "novel_id", nullable = false)
	private Novel novel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "episode_id", nullable = false)
	private Episode episode;
}
