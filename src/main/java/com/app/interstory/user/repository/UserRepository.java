package com.app.interstory.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.user.domain.entity.User;

public interface UserRepository  extends JpaRepository<User, Long> , UserRepositoryCustom{
    
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String username);


}
