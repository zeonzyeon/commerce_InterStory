package com.app.interstory.user.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public class ReadNovelResponseDTO {
	String title;
	String author;
	Integer episodeCount;
	Integer likeCount;
	List<String> tags;
	String thumbnailUrl;
	Integer lastReadEpisode;
}
