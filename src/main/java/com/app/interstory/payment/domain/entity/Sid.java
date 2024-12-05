package com.app.interstory.payment.domain.entity;

import com.app.interstory.user.domain.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "sid")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sid {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sid_id")
	private Long sidId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "sid", nullable = false)
	private String sid;
}
