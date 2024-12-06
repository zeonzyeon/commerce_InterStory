package com.app.interstory.user.repository;

import com.app.interstory.user.domain.entity.Social;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialRepository extends JpaRepository<Social, Long> {
}
