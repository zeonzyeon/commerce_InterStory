package com.app.interstory.novel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.RecentNovel;
import com.app.interstory.user.domain.entity.User;

@Repository
public interface RecentNovelRepository extends JpaRepository<RecentNovel, Long> {
	Optional<RecentNovel> findByUserAndNovel(User user, Novel novel);

	@EntityGraph(attributePaths = {"novel"})
	Page<RecentNovel> findByUser(User user, Pageable pageable);
}
