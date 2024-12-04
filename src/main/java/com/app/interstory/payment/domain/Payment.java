package com.app.interstory.payment.domain;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;

import com.app.interstory.user.domain.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long paymentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@Column(name = "user_id", nullable = false)
	private User user;

	@CreatedDate
	@Column(name = "date", nullable = false, updatable = false)
	private Timestamp date;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "description", nullable = false)
	private String description;
}