package com.app.interstory.novel.repository;

import java.util.Optional;

import com.app.interstory.novel.domain.entity.Novel;

public interface NovelRepositoryCustom {

	Optional<Novel> findByNovelWithUser(Long novelId);

}
