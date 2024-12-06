package com.app.interstory.user.domain.entity;

import org.springframework.data.annotation.CreatedDate;

import com.app.interstory.user.domain.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.sql.Timestamp;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User{

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
    private Long point= DEFAULT_POINT;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "role", nullable = false)
    private Roles role = DEFAULT_ROLE;

    @Builder.Default
    @Column(name = "is_activity", nullable = false)
    private boolean isActivity = DEFAULT_IS_ACTIVITY;

    @Column(name = "profile_renamed_filename")
    private String profileRenamedFilename;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp createdAt;

    @Builder.Default
    @Column(name = "subscribe", nullable = false)
    private boolean subscribe = DEFAULT_SUBSCRIBE;

    @Builder.Default
    @Column(name = "auto_payment", nullable = false)
    private boolean autoPayment = DEFAULT_AUTO_PAYMENT;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private Social social;

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


}
