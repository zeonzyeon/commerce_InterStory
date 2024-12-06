package com.app.interstory.episode.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeRequestDTO {
	private Long novelId;
	private String title;
	private String thumbnailRenamedFilename;
	private String thumbnailUrl;
	private String content;
	private Boolean status;
}
