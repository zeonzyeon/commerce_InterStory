package com.app.interstory.novel.repository;

import java.util.Optional;

import com.app.interstory.novel.domain.entity.Novel;

public interface NovelRepositoryQueryDslCustom {

	Optional<Novel> findByNovelWithUser(Long novelId);

}
