package com.app.interstory.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
	private static final Integer DEFAULT_VIEW_COUNT = 0;
	private static final Long DEFAULT_FEE = 0L;
	private static final String DEFAULT_ACCOUNT_NUMBER = "";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "settlement_id")
	private Long settlementId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder.Default
	@Column(name = "view_count", nullable = false)
	private Integer viewCount = DEFAULT_VIEW_COUNT;

	@Builder.Default
	@Column(name = "fee", nullable = false)
	private Long fee = DEFAULT_FEE;

	@Builder.Default
	@Column(name = "account_number", nullable = false)
	private String accountNumber = DEFAULT_ACCOUNT_NUMBER;

	public void updateAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void requestSettlement() {
		this.viewCount = 0;
		this.fee = 0L;
	}

	public void addViewCount(Boolean isFree) {
		this.viewCount += 1;
		long valance = isFree ? 20L : 40L;
		this.fee += valance;
	}

	public void addUser(User user) {
		this.user = user;
	}
}
