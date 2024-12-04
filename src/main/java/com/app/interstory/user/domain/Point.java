package com.app.interstory.user.domain;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
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
