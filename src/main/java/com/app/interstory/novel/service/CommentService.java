package com.app.interstory.novel.service;

import static com.app.interstory.util.Utils.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Comment;
import com.app.interstory.novel.domain.entity.CommentLike;
import com.app.interstory.novel.domain.entity.Episode;
import com.app.interstory.novel.domain.enumtypes.Sort;
import com.app.interstory.novel.dto.request.CommentRequestDto;
import com.app.interstory.novel.dto.response.CommentListResponseDto;
import com.app.interstory.novel.dto.response.CommentResponseDto;
import com.app.interstory.novel.repository.CommentLikeRepository;
import com.app.interstory.novel.repository.CommentRepository;
import com.app.interstory.novel.repository.EpisodeRepository;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.domain.enumtypes.Roles;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final EpisodeRepository episodeRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final UserRepository userRepository;

	@Transactional
	public void writeComment(CommentRequestDto requestDto, long episodeId, CustomUserDetails userDetails) {

		if (userDetails == null) {
			throw new IllegalStateException("로그인이 필요한 서비스입니다.");
		}

		User user = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 사용자입니다."));

		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("해당 에피소드를 찾을 수 없습니다."));

		Comment comment = Comment.builder().episode(episode).user(user).content(requestDto.getContent()).build();

		commentRepository.save(comment);
	}

	@Transactional
	public CommentListResponseDto getEpisodeComment(Long episodeId, Sort sort, Integer page,
		CustomUserDetails userDetails) {

		final int getItemCount = 4;

		Pageable pageable = PageRequest.of(page, getItemCount);

		Page<Comment> comments = commentRepository.findCommentsByEpisodeId(episodeId, sort.getDescription(), pageable);

		if (page > comments.getTotalPages()) {
			throw new RuntimeException("유효하지 않은 페이지입니다.");
		}

		User user;

		if (userDetails != null) {
			user = userRepository.findById(userDetails.getUser().getUserId())
				.orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 사용자입니다."));
		} else {
			user = null;
		}

		List<CommentResponseDto> commentResponseDtos = comments.getContent().stream().map(comment -> {

			if (!comment.getStatus() && (user == null || user.getRole() != Roles.ADMIN)) {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content("삭제된 댓글입니다.")
					.createdAt(formatTimestamp(comment.getCreatedAt()))
					.likeCount(comment.getLikeCount())
					.isLiked(false)
					.status(comment.getStatus())
					.build();

			} else {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content(comment.getContent())
					.createdAt(formatTimestamp(comment.getCreatedAt()))
					.likeCount(comment.getLikeCount())
					.isLiked(commentLikeRepository.existsByCommentAndUser(comment, user))
					.status(comment.getStatus())
					.build();
			}
		}).toList();

		return CommentListResponseDto.from(commentResponseDtos, comments.getTotalPages());
	}

	@Transactional
	public CommentListResponseDto getNovelComment(Long novelId, Sort sort, Integer page,
		CustomUserDetails userDetails) {

		final int getItemCount = 4;

		Pageable pageable = PageRequest.of(page, getItemCount);

		Page<Comment> comments = commentRepository.findCommentsByNovelId(novelId, sort.getDescription(), pageable);

		if (page > comments.getTotalPages()) {
			throw new RuntimeException("유효하지 않은 페이지입니다.");
		}

		User user;

		if (userDetails != null) {
			user = userRepository.findById(userDetails.getUser().getUserId())
				.orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 사용자입니다."));
		} else {
			user = null;
		}

		List<CommentResponseDto> commentResponseDtos = comments.getContent().stream().map(comment -> {
			if (!comment.getStatus() && (user == null || user.getRole() != Roles.ADMIN)) {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content("삭제된 댓글입니다.")
					.episodeTitle(comment.getEpisode().getTitle())
					.createdAt(formatTimestamp(comment.getCreatedAt()))
					.likeCount(comment.getLikeCount())
					.isLiked(false)
					.status(comment.getStatus())
					.build();

			} else {
				return CommentResponseDto.builder()
					.nickname(comment.getUser().getNickname())
					.profileUrl(comment.getUser().getProfileUrl())
					.content(comment.getContent())
					.episodeTitle(comment.getEpisode().getTitle())
					.createdAt(formatTimestamp(comment.getCreatedAt()))
					.likeCount(comment.getLikeCount())
					.isLiked(commentLikeRepository.existsByCommentAndUser(comment, user))
					.status(comment.getStatus())
					.build();
			}
		}).toList();

		return CommentListResponseDto.from(commentResponseDtos, comments.getTotalPages());
	}

	@Transactional
	public void deleteComment(Long commentId, CustomUserDetails userDetails) {

		if (userDetails == null) {
			throw new IllegalStateException("로그인이 필요한 서비스입니다.");
		}

		User user = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 사용자입니다."));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RuntimeException("해당 댓글을 발견하지 못했습니다."));

		if (comment.getUser().getUserId().equals(user.getUserId()) || user.getRole() == Roles.ADMIN) {
			comment.deleteComment();
		} else {
			throw new IllegalStateException("삭제 권한이 없습니다.");
		}
	}

	@Transactional
	public String likeComment(Long commentId, CustomUserDetails userDetails) {

		if (userDetails == null) {
			throw new IllegalStateException("로그인이 필요한 서비스입니다.");
		}

		String afterLikeMessage;

		User user = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 사용자입니다."));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RuntimeException("해당 댓글을 발견하지 못했습니다."));

		if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
			commentLikeRepository.deleteByUser(user);
			comment.setCommentLike(comment.getLikeCount() - 1);
			afterLikeMessage = "댓글 추천이 취소되었습니다.";
		} else {
			commentLikeRepository.save(new CommentLike(comment, user));
			afterLikeMessage = "댓글 추천이 완료되었습니다.";
			comment.setCommentLike(comment.getLikeCount() + 1);
		}
		commentRepository.save(comment);
		return afterLikeMessage;
	}
}