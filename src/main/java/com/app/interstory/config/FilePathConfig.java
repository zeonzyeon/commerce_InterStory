package com.app.interstory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class FilePathConfig {

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${cloud.aws.region.static}")
	private String region;

	//https://est-team4-bucket.s3.ap-northeast-2.amazonaws.com/profile/user.png
	private final String basePath = String.format("https://%s.s3.%s.amazonaws.com", bucketName, region);

	private final String userProfilePath = basePath + "/user/";
	// 기본이미지 저장
	private final String userDefaultProfilePath = userProfilePath + "user.png";
	private final String thumbnailPath = basePath + "/thumbnail/";
	private final String common = basePath + "/common/";

}
