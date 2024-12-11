package com.app.interstory.user.repository;

import java.util.Optional;

import com.app.interstory.user.domain.entity.User;

public interface UserRepositoryCustom {

	Optional<User> findByIdWithSocial(Long userId);

}
