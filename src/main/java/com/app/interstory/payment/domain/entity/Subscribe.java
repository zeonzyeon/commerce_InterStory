package com.app.interstory.payment.domain.entity;

import com.app.interstory.user.domain.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "subscribe")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscribe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscribe_id")
	private Long subscribeId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "end_at", nullable = false)
	private Timestamp endAt;
}
