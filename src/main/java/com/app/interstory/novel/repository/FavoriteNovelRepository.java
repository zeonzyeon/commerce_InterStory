package com.app.interstory.novel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.FavoriteNovel;
import com.app.interstory.user.domain.entity.User;

@Repository
public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, Long> {
	Page<FavoriteNovel> findByUser(User user, Pageable pageable);
}
