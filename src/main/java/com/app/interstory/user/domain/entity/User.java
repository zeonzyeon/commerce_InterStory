package com.app.interstory.user.domain.entity;

import org.springframework.data.annotation.CreatedDate;

import com.app.interstory.user.domain.enumtypes.Roles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Roles DEFAULT_ROLE = Roles.PUBLIC;
	private static final Long DEFAULT_POINT = 0L;
	private static final Boolean DEFAULT_IS_ACTIVITY = true;
	private static final Boolean DEFAULT_SUBSCRIBE = false;
	private static final Boolean DEFAULT_AUTO_PAYMENT = false;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "nickname", nullable = false, unique = true)
	private String nickname;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "point", nullable = false)
	@Builder.Default
	private Long point = DEFAULT_POINT;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(name = "role", nullable = false)
	private Roles role = DEFAULT_ROLE;

	@Builder.Default
	@Column(name = "is_activity", nullable = false)
	private Boolean isActivity = DEFAULT_IS_ACTIVITY;

	@Column(name = "profile_renamed_filename")
	private String profileRenamedFilename;

	@Column(name = "profile_url")
	private String profileUrl;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreatedDate
	private Timestamp createdAt;

	@Builder.Default
	@Column(name = "subscribe", nullable = false)
	private Boolean subscribe = DEFAULT_SUBSCRIBE;

	@Builder.Default
	@Column(name = "auto_payment", nullable = false)
	private Boolean autoPayment = DEFAULT_AUTO_PAYMENT;

	//business method
	public void update(String profileUrl, String nickname, String password) {
		this.profileUrl = profileUrl;
		this.nickname = nickname;
		this.password = password;
	}

}
