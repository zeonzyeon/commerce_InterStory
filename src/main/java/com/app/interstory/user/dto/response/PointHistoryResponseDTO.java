package com.app.interstory.user.dto.response;

import java.sql.Timestamp;

import lombok.Builder;

@Builder
public class PointHistoryResponseDTO {
	String pointChange;
	String description;
	Timestamp date;
}
