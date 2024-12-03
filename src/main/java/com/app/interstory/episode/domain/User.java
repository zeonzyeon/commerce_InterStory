package com.app.interstory.episode.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "email", nullable = false, columnDefinition = "VARCHAR(255)")
	private String email;

	@Column(name = "password", columnDefinition = "VARCHAR(255)")
	private String password;

	@Column(name = "nickname", nullable = false, columnDefinition = "VARCHAR(255)")
	private String nickname;

	@Column(name = "point", nullable = false)
	private Long point;

	@Column(name = "is_activity", columnDefinition = "TINYINT(1)")
	private Boolean isActive;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private UserRole role;

	@Column(name = "profile_renamed_filename", columnDefinition = "VARCHAR(255)")
	private String profileRenamedFilename;

	@Column(name = "profile_url", columnDefinition = "VARCHAR(255)")
	private String profileUrl;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private Timestamp createdAt;

	@Column(name = "subscribe", columnDefinition = "TINYINT(1)")
	private Boolean subscribe;

	@Column(name = "auto_payment", columnDefinition = "TINYINT(1)")
	private Boolean autoPayment;

	public enum UserRole {
		ADMIN,
		USER
	}
}
