package com.app.interstory.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Social {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "social_id")
	private Long socialId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "provider", nullable = false)
	private Provider provider;

	@Column(name = "client_id", nullable = false)
	private String clientId;
}
