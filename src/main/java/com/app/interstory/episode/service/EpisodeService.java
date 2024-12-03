package com.app.interstory.episode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.interstory.episode.domain.Episode;
import com.app.interstory.episode.domain.Payment;
import com.app.interstory.episode.domain.Point;
import com.app.interstory.episode.domain.User;
import com.app.interstory.episode.domain.dto.EpisodeRequestDTO;
import com.app.interstory.episode.domain.dto.EpisodeResponseDTO;
import com.app.interstory.episode.repository.CollectionRepository;
import com.app.interstory.episode.repository.EpisodeRepository;
import com.app.interstory.episode.repository.PaymentRepository;
import com.app.interstory.episode.repository.PointRepository;
import com.app.interstory.episode.repository.UserRepository;

@Service
public class EpisodeService {
	private final EpisodeRepository episodeRepository;
	private final UserRepository userRepository;
	private final CollectionRepository collectionRepository;
	private final PointRepository pointRepository;
	private final PaymentRepository paymentRepository;

	public EpisodeService(EpisodeRepository episodeRepository, UserRepository userRepository,
		CollectionRepository collectionRepository, PointRepository pointRepository, PaymentRepository paymentRepository) {
		this.episodeRepository = episodeRepository;
		this.userRepository = userRepository;
		this.collectionRepository = collectionRepository;
		this.pointRepository = pointRepository;
		this.paymentRepository = paymentRepository;
	}

	// 회차 수정
	public EpisodeResponseDTO updateEpisode(Long novelId, Long episodeId, EpisodeRequestDTO requestDTO) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovelId().equals(novelId)) {
			throw new IllegalArgumentException("Invalid novelId for the given episode.");
		}

		episode.setTitle(requestDTO.getTitle());
		episode.setThumbnailRenamedFilename(requestDTO.getThumbnailRenamedFilename());
		episode.setThumbnailUrl(requestDTO.getThumbnailUrl());
		episode.setContent(requestDTO.getContent());
		episode.setStatus(requestDTO.getStatus());

		Episode updatedEpisode = episodeRepository.save(episode);
		return convertToDTO(updatedEpisode);
	}

	// 회차 상세 조회
	public EpisodeResponseDTO readEpisode(Long novelId, Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovelId().equals(novelId)) {
			throw new RuntimeException("Episode does not belong to the specified novel");
		}

		return convertToDTO(episode);
	}

	private EpisodeResponseDTO convertToDTO(Episode episode) {
		return EpisodeResponseDTO.builder()
			.episodeId(episode.getEpisodeId())
			.novelId(episode.getNovelId())
			.title(episode.getTitle())
			.thumbnailUrl(episode.getThumbnailUrl())
			.content(episode.getContent())
			.status(episode.getStatus())
			.build();
	}

	// 회차 삭제
	public void deleteEpisode(Long novelId, Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId)
			.orElseThrow(() -> new RuntimeException("Episode not found"));

		if (!episode.getNovelId().equals(novelId)) {
			throw new RuntimeException("Episode does not belong to the specified novel");
		}

		episodeRepository.delete(episode);
	}
}
