package com.app.interstory.ai.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReactionAnalysisDto {

	private final Long episodeId;
	private final String aiAnalysis;
	private final LocalDateTime analyzedAt;

}
