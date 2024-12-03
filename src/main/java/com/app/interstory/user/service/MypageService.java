package com.app.interstory.user.service;

import org.springframework.stereotype.Service;

import com.app.interstory.user.domain.User;
import com.app.interstory.user.dto.request.UpdateUserRequestDTO;
import com.app.interstory.user.dto.response.UpdateUserResponseDTO;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MypageService {
	private final UserRepository userRepository;

	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found id: " + id));
	}

	public UpdateUserResponseDTO updateUser(User user, UpdateUserRequestDTO updateUserRequestDTO) {
		user.update(
			updateUserRequestDTO.getProfileUrl(),
			updateUserRequestDTO.getNickname(),
			updateUserRequestDTO.getPassword()
		);

		userRepository.save(user);

		return new UpdateUserResponseDTO(user.getProfileUrl(), user.getNickname(), user.getPassword());
	}
}
