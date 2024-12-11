package com.app.interstory.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.domain.enumtypes.Roles;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	Optional<User> findByEmail(String username);

	Page<User> findByRoleNot(Roles role, Pageable pageable);

	@Query("SELECT u FROM User u WHERE " +
		"(:nickname IS NULL OR u.nickname LIKE %:nickname%) AND " +
		"(:email IS NULL OR u.email LIKE %:email%) AND " +
		"u.role <> 'ADMIN'")
	Page<User> searchUsers(@Param("nickname") String nickname,
		@Param("email") String email,
		Pageable pageable);

}
