package com.app.interstory.user.dto.response;

import java.sql.Timestamp;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyNovelResponseDTO {
	Long novelId;
	String title;
	Integer likeCount;
	List<String> tags;
	String thumbnailUrl;
	Timestamp episodeUpdatedAt;
	Integer favoriteCount;
}
