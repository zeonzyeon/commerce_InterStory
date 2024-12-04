package com.app.interstory.novel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.FavoriteNovel;
import com.app.interstory.user.domain.User;

@Repository
public interface FavoriteNovelRepository extends JpaRepository<FavoriteNovel, Long> {
	List<FavoriteNovel> findByUser(User user);
}
