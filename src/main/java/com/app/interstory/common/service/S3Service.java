package com.app.interstory.common.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.app.interstory.util.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class S3Service {

	private final AmazonS3Client amazonS3Client;
	private final String bucket;

	public S3Service(AmazonS3Client amazonS3Client,
		@Value("${cloud.aws.s3.bucket}") String bucket) {
		this.amazonS3Client = amazonS3Client;
		this.bucket = bucket;
	}

	//파일 업로드
	public String uploadFile(MultipartFile file, String dirPath) {
		try {
			String fileName = Utils.getRenameFilename(Objects.requireNonNull(file.getOriginalFilename()));
			dirPath += fileName;
			System.out.println("**************경로 확인 ::" + dirPath);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(file.getContentType());
			metadata.setContentLength(file.getSize());

			amazonS3Client.putObject(
				new PutObjectRequest(bucket, dirPath, file.getInputStream(), metadata)
			);

			return amazonS3Client.getUrl(bucket, fileName).toString();
		} catch (IOException e) {
			throw new RuntimeException("파일 업로드 실패", e);
		}
	}

	//파일 삭제
	public void deleteFile(String fileUrl, String dirPath) {
		try {
			String fileName = extractFileNameFromUrl(fileUrl);
			amazonS3Client.deleteObject(bucket, dirPath + fileName);
		} catch (Exception e) {
			log.error("파일 삭제 실패: {}", e.getMessage());
			throw new RuntimeException("파일 삭제 실패", e);
		}
	}

	private String extractFileNameFromUrl(String fileUrl) {
		return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
	}

}