package com.app.interstory.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Entity
@Table(name = "point")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Point {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_id")
	private Long pointId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "used_at", nullable = false, updatable = false)
	@CreatedDate
	private Timestamp usedAt;

	@Column(name = "balance", nullable = false)
	private Long balance;

	@Column(name = "description", nullable = false)
	private String description;
}
