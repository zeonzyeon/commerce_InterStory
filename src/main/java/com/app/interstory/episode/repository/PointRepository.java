package com.app.interstory.episode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.interstory.episode.domain.Point;

public interface PointRepository extends JpaRepository<Point, Long> {
	List<Point> findByUserId(Long userId);
}

