package com.app.interstory.novel.domain.entity;

import com.app.interstory.user.domain.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "collection")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "collection_id")
	private Long collectionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "episode_id", nullable = false)
	private Episode episode;
}
