package com.app.interstory.user.domain.entity;

import com.app.interstory.novel.domain.entity.Episode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id")
	private Long cartItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "episode_id", nullable = false)
	private Episode episode;

	public CartItem(User user, Episode episode) {
		this.user = user;
		this.episode = episode;
	}
}
