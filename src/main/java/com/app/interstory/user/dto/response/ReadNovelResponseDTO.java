package com.app.interstory.user.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReadNovelResponseDTO {
	String title;
	String author;
	Integer episodeCount;
	Integer likeCount;
	List<String> tags;
	String thumbnailUrl;
	Integer lastReadEpisode;
}
