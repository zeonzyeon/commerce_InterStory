package com.app.interstory.novel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.Novel;
import com.app.interstory.novel.domain.RecentNovel;
import com.app.interstory.user.domain.entity.User;

@Repository
public interface RecentNovelRepository extends JpaRepository<RecentNovel, Long> {
	Optional<RecentNovel> findByUserAndNovel(User user, Novel novel);
}
