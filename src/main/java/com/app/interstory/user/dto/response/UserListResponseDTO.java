package com.app.interstory.user.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponseDTO {
    private final List<UserResponseDTO> users;
    private final Integer totalPage;

    public static UserListResponseDTO from(List<UserResponseDTO> users, Integer totalPage) {
        return UserListResponseDTO.builder().users(users).totalPage(totalPage).build();
    }
}
