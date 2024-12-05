package com.app.interstory.user.dto.response;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PointHistoryResponseDTO {
	String pointChange;
	String description;
	Timestamp date;
}
