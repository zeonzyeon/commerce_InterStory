package com.app.interstory.user.dto.response;

import com.app.interstory.user.domain.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class CartItemResponseDTO {
	private Long cartItemId;
	private String novelTitle;
	private Long episodeId;
	private String episodeTitle;

	public static CartItemResponseDTO from(CartItem cartItem) {
		return CartItemResponseDTO.builder()
				.cartItemId(cartItem.getCartItemId())
				.novelTitle(cartItem.getEpisode().getNovel().getTitle())
				.episodeId(cartItem.getEpisode().getEpisodeId())
				.episodeTitle(cartItem.getEpisode().getTitle())
				.build();
	}
}

