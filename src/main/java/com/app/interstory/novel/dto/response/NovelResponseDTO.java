package com.app.interstory.novel.dto.response;

import static com.app.interstory.util.Utils.*;

import java.util.List;

import com.app.interstory.novel.domain.entity.Novel;
import com.app.interstory.novel.domain.enumtypes.MainTag;
import com.app.interstory.novel.domain.enumtypes.NovelStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class NovelResponseDTO {
	private Long novelId;
	private String author;
	private String title;
	private String description;
	private String thumbnailUrl;
	private NovelStatus status;
	private MainTag mainTag;
	private List<String> customTag;
	private String publishedAt;

	public static NovelResponseDTO from(Novel novel, List<String> customTag) {
		return NovelResponseDTO.builder()
			.novelId(novel.getNovelId())
			.author(novel.getUser().getNickname())
			.title(novel.getTitle())
			.description(novel.getDescription())
			.thumbnailUrl(novel.getThumbnailUrl())
			.status(novel.getStatus())
			.mainTag(novel.getTag())
			.customTag(customTag)
			.publishedAt(formatTimestamp(novel.getPublishedAt()))
			.build();
	}

	public static NovelResponseDTO from(Novel novel) {
		return NovelResponseDTO.builder()
			.novelId(novel.getNovelId())
			.author(novel.getUser().getNickname())
			.title(novel.getTitle())
			.description(novel.getDescription())
			.thumbnailUrl(novel.getThumbnailUrl())
			.status(novel.getStatus())
			.mainTag(novel.getTag())
			.publishedAt(formatTimestamp(novel.getPublishedAt()))
			.build();
	}
}
