package com.app.interstory.user.domain.entity;

import com.app.interstory.user.domain.enumtypes.Provider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

	//business method
	public void addUser(User user) {
		this.user = user;
	}

	public void deleteSocial() {
		this.user = null;
	}
}
