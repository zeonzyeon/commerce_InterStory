package com.app.interstory.user.domain;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "nickname", nullable = false, unique = true)
	private String nickname;

	@Column(name = "point", nullable = false)
	private Long point;

	@Builder.Default
	@Column(name = "is_activity", nullable = false)
	private boolean isActivity = true;

	@Builder.Default
	@Column(name = "role", nullable = false)
	private Roles role = Roles.PUBLIC;

	@Column(name = "profile_renamed_filename")
	private String profileRenamedFilename;

	@Column(name = "profile_url")
	private String profileUrl;

	@Column(name = "created_at", nullable = false)
	@CreatedDate
	private String createdAt;

	@Builder.Default
	@Column(name = "subscribe", nullable = false)
	private boolean subscribe = false;

	@Builder.Default
	@Column(name = "auto_payment", nullable = false)
	private boolean autoPayment = false;

	public void update(String profileUrl, String nickname, String password) {
		this.profileUrl = profileUrl;
		this.nickname = nickname;
		this.password = password;
	}
}
