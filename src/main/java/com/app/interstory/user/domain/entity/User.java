package com.app.interstory.user.domain.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.app.interstory.user.domain.enumtypes.Roles;
import com.app.interstory.user.dto.request.withdrawalRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

	private static final Roles DEFAULT_ROLE = Roles.PUBLIC;
	private static final long DEFAULT_POINT = 0L;
	private static final Boolean DEFAULT_IS_ACTIVITY = true;
	private static final Boolean DEFAULT_SUBSCRIBE = false;
	private static final Boolean DEFAULT_AUTO_PAYMENT = false;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "nickname", nullable = false, unique = true)
	private String nickname;

	@Column(name = "password")
	private String password;

	@Column(name = "point", nullable = false)
	@Builder.Default
	private Long point = DEFAULT_POINT;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(name = "role", nullable = false)
	private Roles role = DEFAULT_ROLE;

	@Builder.Default
	@Column(name = "is_activity", nullable = false)
	private Boolean isActivity = DEFAULT_IS_ACTIVITY;

	@Column(name = "profile_renamed_filename")
	private String profileRenamedFilename;

	@Column(name = "profile_url")
	private String profileUrl;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreatedDate
	private Timestamp createdAt;

	@Builder.Default
	@Column(name = "subscribe", nullable = false)
	private Boolean isSubscribe = DEFAULT_SUBSCRIBE;

	@Builder.Default
	@Column(name = "auto_payment", nullable = false)
	private Boolean isAutoPayment = DEFAULT_AUTO_PAYMENT;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Social social;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Settlement settlement;

	//연관관계 method
	public void addSocialProvider(Social social) {
		this.social = social;
		social.addUser(this);
	}

	//business method
	public void update(String profileUrl, String nickname, String password) {
		this.profileUrl = profileUrl;
		this.nickname = nickname;
		this.password = password;
	}

	public void reducePointsForPurchase(Long amount) {
		if (this.point < amount) {
			throw new RuntimeException("Insufficient points");
		}
		this.point -= amount;
	}

	//소셜 회원 탈퇴
	public void deleteSocial() {
		this.social.deleteSocial();
		this.social = null;
	}

	//회원 탈퇴
	public void withdrawal(withdrawalRequest request) {
		this.isActivity = false;
		this.email += "[탈퇴 회원]";
		this.nickname += "[탈퇴 회원]:";
		//적을 공간이 없어서 임시로 저장
		this.profileRenamedFilename = "[탈퇴 사유]:" + request.getReason() + "\n[탈퇴 사유]:" + request.getReasonDetail();
	}

	//기본 이미지 확인
	public Boolean isDefaultProfile() {
		return this.getProfileRenamedFilename().equals("user.png");
	}

	public void active() {
		this.isActivity = true;
	}

	//프로필 정보 업데이트
	public void updateProfile(String filePath) {
		this.profileUrl = filePath;
		this.profileRenamedFilename = filePath.substring(filePath.lastIndexOf("/") + 1);
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}
}
