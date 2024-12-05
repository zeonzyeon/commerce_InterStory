package com.app.interstory.novel.repository;

import com.app.interstory.novel.domain.entity.Comment;
import com.app.interstory.novel.domain.entity.CommentLike;
import com.app.interstory.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentAndUser(Comment comment, User user);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.user = :user")
    void deleteByUser(@Param("user") User user);
}
