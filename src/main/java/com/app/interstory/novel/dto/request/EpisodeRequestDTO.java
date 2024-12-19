package com.app.interstory.novel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeRequestDTO {
	private Long novelId;
	private String title;
	private String content;
}
