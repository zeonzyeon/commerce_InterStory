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
@Table(name = "payment")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long paymentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@CreatedDate
	@Column(name = "date", nullable = false, updatable = false)
	private Timestamp date;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "description", nullable = false)
	private String description;
}
