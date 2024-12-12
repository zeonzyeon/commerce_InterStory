package com.app.interstory.novel.dto.response;

import java.util.List;

import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NovelDetailResponseDTO {
	private Long novelId;
	private String title;
	private String description;
	private String thumbnailUrl;
	private NovelStatus status;
	private MainTag tag;
	private List<EpisodeResponseDTO> episodes;
	private int totalPages;
}

