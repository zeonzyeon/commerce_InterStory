package com.app.interstory.user.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api/image")
@Slf4j
public class ImageController {
	private final String uploadPath;

	ImageController(@Value("${file.upload-dir}") String uploadPath) {
		this.uploadPath = uploadPath;
	}

	@PostMapping("/upload")
	public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
		try {
			Files.createDirectories(Paths.get(uploadPath));

			// 파일 이름 생성 (중복 방지를 위해 UUID 사용)
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

			String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
			if (!isValidImageExtension(fileExtension)) {
				return ResponseEntity.badRequest().body(Map.of("error", "Invalid file type"));
			}

			// 저장할 경로 생성
			Path filePath = Paths.get(uploadPath, fileName);

			// 파일 저장
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// 이미지에 접근할 수 있는 URL 생성
			String imageUrl = "/images/uploaded/" + fileName; // 실제 서버의 URL 구조에 맞게 수정

			return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", "Failed to upload image"));
		}
	}

	private boolean isValidImageExtension(String extension) {
		if (extension == null)
			return false;
		List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
		return validExtensions.contains(extension.toLowerCase());
	}

	@PostMapping("/post")
	public String postImg(@RequestParam("content") String content) {
		log.error(content);
		return "redirect:/";
	}
}
