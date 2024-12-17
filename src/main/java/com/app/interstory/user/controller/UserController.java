package com.app.interstory.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.interstory.novel.dto.response.MyPageNovelResponseDto;
import com.app.interstory.novel.service.EpisodeService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.dto.vo.KakaoAPI;
import com.app.interstory.user.service.AuthenticationService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final KakaoAPI kakaoAPI;
	private final AuthenticationService authenticationService;
	private final EpisodeService episodeService;

	//로그인 페이지
	@GetMapping("auth/login")
	public String login() {
		return "user/login";
	}

	//회원 가입 페이지
	@GetMapping("auth/signup")
	public String signup() {
		return "user/signup";
	}

	//kakao 로그인
	@GetMapping("auth/login/kakao")
	public String kakaoLogin() {
		System.out.println("유알엘 연결확인!!!!!!!!!!!!!!");

		return "redirect:" + kakaoAPI.getAuthorizationUrl();
	}

	//kakao callback
	@GetMapping("auth/kakao/callback")
	public String kakaoCallback(@RequestParam String code,
		HttpSession session) {
		User socialLoginUser = authenticationService.kakaoLogin(code);
		CustomUserDetails customUserDetails = authenticationService.authenticateKakao(socialLoginUser);
		log.info("customUserDetails:SocialLogin :::{}", customUserDetails);
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		return "redirect:/";
	}

	//회원탈퇴 페이지
	@GetMapping("auth/withdrawal")
	public String withdrawal(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		Model model
	) {
		model.addAttribute("user", customUserDetails);

		return "user/withdrawal";
	}

	//내 애피소드 페이지
	@GetMapping("mypage/episode/{novelId}")
	public String myEpisode(
		@PathVariable Long novelId,
		Model model
	) {
		MyPageNovelResponseDto novel = episodeService.findByNovelId(novelId);
		model.addAttribute("novel", novel);

		return "mypage/ai-reaction";
	}

//	// 내 장바구니 페이지
//	@GetMapping("{userId}/cart")
//	public String myCart(@PathVariable Long userId, Model model) {
//		return ""
//	}

}
