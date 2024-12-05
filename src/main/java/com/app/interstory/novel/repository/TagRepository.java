package com.app.interstory.novel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	List<Tag> findByNovel(Novel novel);
}
