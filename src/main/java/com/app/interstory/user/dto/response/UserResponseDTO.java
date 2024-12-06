package com.app.interstory.user.dto.response;

import com.app.interstory.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

import static com.app.interstory.util.Utils.formatTimestamp;

@Getter
@Builder
public class UserResponseDTO {
    private final Long userId;
    private final String nickname;
    private final String email;
    private final Long point;
    private final Boolean isActivity;
    private final String createdAt;

    public static UserResponseDTO from (User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .point(user.getPoint())
                .isActivity(user.getIsActivity())
                .createdAt(formatTimestamp(user.getCreatedAt()))
                .build();
    }
}
