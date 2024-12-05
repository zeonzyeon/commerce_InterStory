package com.app.interstory.user.service;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.domain.enumtypes.Roles;
import com.app.interstory.user.dto.response.UserListResponseDTO;
import com.app.interstory.user.dto.response.UserResponseDTO;
import com.app.interstory.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public UserListResponseDTO getUsers(@RequestParam(defaultValue = "1") Integer page, @AuthenticationPrincipal CustomUserDetails userDetails) {
        final int getUserCount = 10;

        User user = userDetails.getUser();

        if (user == null) {
            throw new IllegalStateException("로그인이 필요한 서비스입니다.");
        } else if (user.getRole() != Roles.ADMIN) {
            throw new IllegalStateException("조회 권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(page - 1, getUserCount);

        Page<User> users = userRepository.findByRoleNot(user.getRole(), pageable);

        if (page > users.getTotalPages()) {
            throw new RuntimeException("유효하지 않은 페이지입니다.");
        }

        List<UserResponseDTO> userList = users.stream()
                .map(UserResponseDTO::from)
                .toList();

        return UserListResponseDTO.from(userList, users.getTotalPages());
    }
}

