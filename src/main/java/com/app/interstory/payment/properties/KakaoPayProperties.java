package com.app.interstory.payment.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties("kakaopay")
@Getter
@Setter
public class KakaoPayProperties {
	private String secretKey;
	private String onetimeCid;
	private String sequenceCid;
	private String subscriptionCid;
}
