package com.app.interstory.user.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyNovelResponseDTO {
	String title;
	Integer likeCount;
	List<String> tags;
	String thumbnailUrl;
	// TODO: REDIS로부터 작품 반응 조회
	// String reaction;
}
