package com.app.interstory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class FilePathConfig {

	private final String bucketName;
	private final String region;
	private final String basePath;
	private final String userProfilePath;
	private final String userDefaultProfilePath;
	private final String thumbnailPath;
	private final String common;

	public FilePathConfig(
		@Value("${cloud.aws.s3.bucket}") String bucketName,
		@Value("${cloud.aws.region.static}") String region) {
		this.bucketName = bucketName;
		this.region = region;
		this.basePath = String.format("https://%s.s3.%s.amazonaws.com", bucketName, region);
		this.userProfilePath = basePath + "/profile/";
		this.userDefaultProfilePath = userProfilePath + "user.png";
		this.thumbnailPath = basePath + "/thumbnail/";
		this.common = basePath + "/common/";
	}

}
