package com.app.interstory.novel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.RecentNovel;

@Repository
public interface RecentNovelRepository extends JpaRepository<RecentNovel, Long> {
	Optional<RecentNovel> findByUser_UserIdAndNovel(Long userId, Novel novel);

	@EntityGraph(attributePaths = {"novel"})
	Page<RecentNovel> findByUser_UserId(Long userId, Pageable pageable);
}
