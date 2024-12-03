package com.app.interstory.episode.domain;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "point")
public class Point {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_id")
	private Long pointId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@CreatedDate
	@Column(name = "used_at", nullable = false, updatable = false)
	private Timestamp usedAt;

	@Column(name = "balance", nullable = false)
	private Long balance;

	@Column(name = "description", nullable = false, columnDefinition = "VARCHAR(255)")
	private String description;
}
