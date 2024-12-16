package com.app.interstory.user.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
	private static final long VERIFICATION_CODE_EXPIRATION = 300L;
	private final RedisTemplate<String, String> redisTemplate;
	private final UserRepository userRepository;
	private final JavaMailSender emailSender;

	//메일 전송 및 코드 저장
	public String sendVerificationEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email already exists");
		}
		String verificationCode = generateVerificationCode();
		log.info("Generated verification code***: {}", verificationCode);
		redisTemplate.opsForValue()
			.set(getRedisKey(email), verificationCode,
				Duration.ofSeconds(VERIFICATION_CODE_EXPIRATION));
		sendEmail(email, verificationCode);
		return verificationCode;
	}

	// 인증 코드 확인
	public boolean verifyCode(String email, String code) {
		String storedCode = redisTemplate.opsForValue().get(getRedisKey(email));
		return code.equals(storedCode);
	}

	private String getRedisKey(String email) {
		return "EMAIL_VERIFICATION:" + email;
	}

	private String generateVerificationCode() {
		return String.format("%06d", new Random().nextInt(999999));
	}

	private void sendEmail(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("이메일 인증 코드");
		message.setText("인증 코드: " + code);
		emailSender.send(message);
	}

}
