package com.app.interstory.user.service;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.vo.UserSessionVo;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User currentUser = userRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		return createUserDetails(currentUser);
	}

	public CustomUserDetails loadUserBySocial(User socialUser) {

		//권한 설정
		Set<GrantedAuthority> authorities = Set.of(
			new SimpleGrantedAuthority("ROLE_" + socialUser.getRole().name())
		);

		return CustomUserDetails.builder()
			.authorities(authorities)
			.user(createUserSessionVo(socialUser))
			.build();
	}

	private CustomUserDetails createUserDetails(User currentUser) {
		//권한 설정
		Set<GrantedAuthority> authorities = Set.of(
			new SimpleGrantedAuthority("ROLE_" + currentUser.getRole().name())
		);

		return CustomUserDetails.builder()
			.authorities(authorities)
			.user(createUserSessionVo(currentUser))
			.build();
	}

	//convert
	public UserSessionVo createUserSessionVo(User user) {
		return new UserSessionVo(
			user.getUserId(),
			user.getEmail(),
			user.getNickname(),
			user.getPassword(),
			user.getRole(),
			user.getProfileUrl()
		);
	}

}
