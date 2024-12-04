package com.app.interstory.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settlement")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Settlement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "settlement_id")
	private Long settlementId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder.Default
	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;

	@Builder.Default
	@Column(name = "fee", nullable = false)
	private Long fee = 0L;

	@Builder.Default
	@Column(name = "account_number", nullable = false)
	private String accountNumber = "";
}
