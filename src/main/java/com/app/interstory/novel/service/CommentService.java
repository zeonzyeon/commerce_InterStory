package com.app.interstory.novel.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.Comment;
import com.app.interstory.novel.domain.CommentLike;
import com.app.interstory.novel.domain.Episode;
import com.app.interstory.novel.domain.Sort;
import com.app.interstory.novel.dto.request.CommentRequestDto;
import com.app.interstory.novel.dto.response.CommentListResponseDto;
import com.app.interstory.novel.dto.response.CommentResponseDto;
import com.app.interstory.novel.repository.CommentLikeRepository;
import com.app.interstory.novel.repository.CommentRepository;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.user.domain.Roles;
import com.app.interstory.user.domain.User;
import com.app.interstory.user.domain.UserDetail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final EpisodeRepository episodeRepository;
	private final CommentLikeRepository commentLikeRepository;

	@Transactional
	public void writeComment(CommentRequestDto requestDto, long episodeId, UserDetail userDetails) {
		User user = userDetails.getUser();

		if (user == null) {
			throw new IllegalStateException("로그인이 필요한 서비스입니다.");
		}

		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("해당 에피소드를 찾을 수 없습니다."));

		Comment comment = Comment.builder().episode(episode).user(user).content(requestDto.getContent()).build();

		commentRepository.save(comment);
	}

	@Transactional
	public CommentListResponseDto getEpisodeComment(Long episodeId, Sort sort, Integer page, UserDetail userDetails) {
		final int getItemCount = 4;

		User user = userDetails.getUser();

		Pageable pageable = PageRequest.of(page - 1, getItemCount);

		Page<Comment> comments = commentRepository.findCommentsByEpisodeId(episodeId, sort.getDescription(), pageable);

		List<CommentResponseDto> commentResponseDtos = comments.getContent().stream().map(comment -> {
			Integer likeCount = commentLikeRepository.findAllByComment(comment);

			if (!comment.getStatus() && user != null && user.getRole() == Roles.ADMIN) {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content(comment.getContent())
					.createdAt(comment.getCreatedAt())
					.likeCount(likeCount)
					.isLiked(commentLikeRepository.existsByCommentAndUser(comment, user))
					.status(comment.getStatus())
					.build();
			} else {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content("삭제된 댓글입니다.")
					.createdAt(comment.getCreatedAt())
					.likeCount(likeCount)
					.isLiked(false)
					.status(comment.getStatus())
					.build();
			}
		}).toList();

		return CommentListResponseDto.from(commentResponseDtos, comments.getTotalPages());
	}

	@Transactional
	public CommentListResponseDto getNovelComment(Long novelId, Sort sort, Integer page, UserDetail userDetails) {
		final int getItemCount = 4;

		User user = userDetails.getUser();

		Pageable pageable = PageRequest.of(page - 1, getItemCount);

		Page<Comment> comments = commentRepository.findCommentsByNovelId(novelId, sort.getDescription(), pageable);

		List<CommentResponseDto> commentResponseDtos = comments.getContent().stream().map(comment -> {
			Integer likeCount = commentLikeRepository.findAllByComment(comment);

			if (!comment.getStatus() && user != null && user.getRole() == Roles.ADMIN) {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content(comment.getContent())
					.createdAt(comment.getCreatedAt())
					.likeCount(likeCount)
					.isLiked(commentLikeRepository.existsByCommentAndUser(comment, user))
					.status(comment.getStatus())
					.build();
			} else {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content("삭제된 댓글입니다.")
					.createdAt(comment.getCreatedAt())
					.likeCount(likeCount)
					.isLiked(false)
					.status(comment.getStatus())
					.build();
			}
		}).toList();

		return CommentListResponseDto.from(commentResponseDtos, comments.getTotalPages());
	}

	@Transactional
	public void deleteComment(Long commentId, UserDetail userDetails) {
		User user = userDetails.getUser();

		if (user == null) {
			throw new IllegalStateException("로그인이 필요한 서비스입니다.");
		}

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RuntimeException("해당 댓글을 발견하지 못했습니다."));

		if (comment.getUser().getUserId().equals(user.getUserId()) || user.getRole() == Roles.ADMIN) {
			comment.deleteComment();
		} else {
			throw new IllegalStateException("삭제 권한이 없습니다.");
		}
	}

	@Transactional
	public String likeComment(Long commentId, UserDetail userDetails) {
		String afterLikeMessage;

		User user = userDetails.getUser();

		if (user == null) {
			throw new IllegalStateException("로그인이 필요한 서비스입니다.");
		}

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RuntimeException("해당 댓글을 발견하지 못했습니다."));

		if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
			commentLikeRepository.deleteByUser(user);
			afterLikeMessage = "댓글 추천이 취소되었습니다.";
		} else {
			commentLikeRepository.save(new CommentLike(user, comment));
			afterLikeMessage = "댓글 추천이 완료되었습니다.";
		}

		return afterLikeMessage;
	}
}