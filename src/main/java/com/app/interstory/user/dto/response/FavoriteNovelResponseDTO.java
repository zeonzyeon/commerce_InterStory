package com.app.interstory.user.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FavoriteNovelResponseDTO {
	String title;
	String author;
	Integer episodeCount;
	Integer likeCount;
	List<String> tags;
	String thumbnailUrl;
	Long lastReadEpisodeId;
	Integer lastReadEpisodeNumber;
}
