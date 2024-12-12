package com.app.interstory.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {
	private Long cartItemId;
	private Long episodeId;
	private String episodeTitle;
}

