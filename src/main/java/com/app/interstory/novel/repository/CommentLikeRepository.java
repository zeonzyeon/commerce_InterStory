package com.app.interstory.novel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.interstory.novel.domain.Comment;
import com.app.interstory.novel.domain.CommentLike;
import com.app.interstory.user.domain.User;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
	boolean existsByCommentAndUser(Comment comment, User user);

	@Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment = :comment")
	Integer findAllByComment(@Param("comment") Comment comment);

	@Modifying
	@Query("DELETE FROM CommentLike cl WHERE cl.user = :user")
	void deleteByUser(@Param("user") User user);
}
