package com.app.interstory.novel.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EpisodeListResponseDTO {
	private List<NovelEpisodeResponseDTO> episodeList;
	private Integer totalPage;
	private boolean showAll;
}
