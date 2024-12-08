package com.app.interstory.novel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.dto.request.NovelRequestDTO;
import com.app.interstory.novel.repository.NovelRepository;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NovelService {
	private final NovelRepository novelRepository;
	private final UserRepository userRepository;

	// 소설 작성
	@Transactional
	public Long writeNovel(NovelRequestDTO novelRequestDTO) {
		User user = userRepository.findById(novelRequestDTO.getUserId())
			.orElseThrow(() -> new RuntimeException("User not found"));

		Novel novel = Novel.builder()
			.user(user)
			.title(novelRequestDTO.getTitle())
			.description(novelRequestDTO.getDescription())
			.plan(novelRequestDTO.getPlan())
			.status(NovelStatus.DRAFT)
			.thumbnailRenamedFilename(novelRequestDTO.getThumbnailRenamedFilename())
			.thumbnailUrl(novelRequestDTO.getThumbnailUrl())
			.tag(novelRequestDTO.getTag())
			.build();

		novelRepository.save(novel);
		return novel.getNovelId();
	}

	// 소설 수정
	public void updateNovel(Long novelId, NovelRequestDTO novelRequestDTO) {
		Novel novel = novelRepository.findById(novelId)
			.orElseThrow(() -> new RuntimeException("Novel not found"));

		novel.update(
			novelRequestDTO.getTitle(),
			novelRequestDTO.getDescription(),
			novelRequestDTO.getPlan(),
			novelRequestDTO.getThumbnailRenamedFilename(),
			novelRequestDTO.getThumbnailUrl(),
			novelRequestDTO.getTag(),
			novelRequestDTO.getStatus()
		);
	}
}
