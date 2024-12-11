package com.app.interstory.payment.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.app.interstory.payment.domain.entity.Payment;
import com.app.interstory.payment.domain.entity.Sid;
import com.app.interstory.payment.domain.enumtypes.PaymentType;
import com.app.interstory.payment.domain.repository.PaymentRepository;
import com.app.interstory.payment.domain.repository.SidRepository;
import com.app.interstory.payment.dto.response.PaymentAproveResponseDTO;
import com.app.interstory.payment.dto.response.PaymentInactiveResponseDTO;
import com.app.interstory.payment.dto.response.PaymentReadyResponseDTO;
import com.app.interstory.payment.properties.KakaoPayProperties;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoService {
	private static final String NOT_FOUND_USER_ID = "해당 사용자가 없습니다. id : ";
	private static final Long POINT_PRICE_FIRST = 1_000L;
	private static final Long POINT_PRICE_SECOND = 5_000L;
	private static final Long POINT_PRICE_THIRD = 10_000L;
	private static final Long POINT_SEQUENCE_PRICE = 14_999L;
	private static final Long POINT_AUTO_PRICE = 9_999L;
	private final KakaoPayProperties kakaoPayProperties;
	private final SidRepository sidRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final PointRepository pointRepository;
	private PaymentReadyResponseDTO paymentReadyResponseDTO;

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();

		String auth = "SECRET_KEY " + kakaoPayProperties.getSecretKey();
		headers.set("Authorization", auth);
		headers.set("Content-type", "application/json");

		return headers;
	}

	public PaymentReadyResponseDTO kakaoPayReady(Long userId, PaymentType type) {
		String cid = kakaoPayProperties.getOnetimeCid();
		String name = "포인트 결제";
		Long totalPrice;
		if (type == PaymentType.NORMAL_FIRST) {
			totalPrice = POINT_PRICE_FIRST;
		} else if (type == PaymentType.NORMAL_SECOND) {
			totalPrice = POINT_PRICE_SECOND;
		} else if (type == PaymentType.NORMAL_THIRD) {
			totalPrice = POINT_PRICE_THIRD;
		} else if (type == PaymentType.SEQUENCE) {
			cid = kakaoPayProperties.getSequenceCid();
			name = "포인트 정기 결제";
			totalPrice = POINT_SEQUENCE_PRICE;
		} else {
			cid = kakaoPayProperties.getSubscriptionCid();
			name = "포인트 자동 결제";
			totalPrice = POINT_AUTO_PRICE;
		}

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("cid", cid);                                                 // 가맹점 코드(테스트용)
		parameters.put("partner_order_id", "1234567890");                           // 주문번호(테이블과 매칭 필요)
		parameters.put("partner_user_id", String.valueOf(userId));                  // 회원 아이디
		parameters.put("item_name", name);                                          // 상품명
		parameters.put("quantity", "1");                                            // 상품 수량
		parameters.put("total_amount", String.valueOf(totalPrice));                 // 상품 총액
		parameters.put("tax_free_amount", "0");                                     // 상품 비과세 금액
		parameters.put("approval_url", "http://localhost:8080/api/cash/success");   // 결제 성공 시 URL
		parameters.put("cancel_url", "http://localhost:8080/api/cash/cancel");      // 결제 취소 시 URL
		parameters.put("fail_url", "http://localhost:8080/api/cash/fail");          // 결제 실패 시 URL

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

		RestTemplate template = new RestTemplate();

		String url = "https://open-api.kakaopay.com/online/v1/payment/ready";

		paymentReadyResponseDTO = template.postForObject(url, requestEntity, PaymentReadyResponseDTO.class);

		return paymentReadyResponseDTO;
	}

	public PaymentAproveResponseDTO kakaoPayApprove(Long userId, String pgToken) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		Map<String, String> parameters = new HashMap<>();

		parameters.put("cid", kakaoPayProperties.getOnetimeCid());
		parameters.put("tid", paymentReadyResponseDTO.getTid());
		parameters.put("partner_order_id", "1234567890");
		parameters.put("partner_user_id", String.valueOf(userId));
		parameters.put("pg_token", pgToken);

		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

		RestTemplate template = new RestTemplate();

		String url = "https://open-api.kakaopay.com/online/v1/payment/approve";

		PaymentAproveResponseDTO response = template.postForObject(url, requestEntity, PaymentAproveResponseDTO.class);

		if (response != null && response.getSid() != null) {
			Sid sid = Sid.builder()
				.user(user)
				.sid(response.getSid())
				.build();

			sidRepository.save(sid);
		}

		LocalDateTime localDateTime = LocalDateTime.parse(response.getApproved_at());
		Timestamp timestamp = Timestamp.valueOf(localDateTime);

		// 결제 테이블에 save
		Payment payment = Payment.builder()
			.user(user)
			.date(timestamp)
			.amount(Long.valueOf(response.getAmount().getTotal()))
			.description(response.getItem_name())
			.build();

		Integer amount = response.getAmount().getTotal();
		if (amount == POINT_AUTO_PRICE.intValue()) {
			Point point = Point.builder()
				.user(user)
				.usedAt(timestamp)
				.balance(POINT_AUTO_PRICE)
				.description(POINT_AUTO_PRICE + " 포인트 자동 충전")
				.build();

			pointRepository.save(point);
		} else if (amount != POINT_SEQUENCE_PRICE.intValue()) {
			Long price;
			if (amount == POINT_PRICE_FIRST.intValue())
				price = POINT_PRICE_FIRST;
			else if (amount == POINT_PRICE_SECOND.intValue())
				price = POINT_PRICE_SECOND;
			else
				price = POINT_PRICE_THIRD;

			Point point = Point.builder()
				.user(user)
				.usedAt(timestamp)
				.balance(price)
				.description(price + " 포인트 충전")
				.build();

			pointRepository.save(point);
		}

		paymentRepository.save(payment);

		return response;
	}

	public Boolean checkSid(Long userId) {
		return sidRepository.existsByUser_UserId(userId);
	}

	public PaymentAproveResponseDTO kakaoPayPayment(Long userId, PaymentType type) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));
		Sid sid = sidRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		String name;
		Long totalPrice;
		if (type == PaymentType.SEQUENCE) {
			name = "구독 서비스 결제";
			totalPrice = POINT_SEQUENCE_PRICE;
		} else {
			name = "자동 결제 서비스 결제";
			totalPrice = POINT_AUTO_PRICE;
		}

		Map<String, String> parameters = new HashMap<>();

		parameters.put("cid", kakaoPayProperties.getSubscriptionCid());
		parameters.put("sid", sid.getSid());
		parameters.put("partner_order_id", "1234567890");
		parameters.put("partner_user_id", String.valueOf(userId));
		parameters.put("item_name", name);
		parameters.put("quantity", "1");
		parameters.put("total_amount", String.valueOf(totalPrice));
		parameters.put("tax_free_amount", "0");

		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

		RestTemplate template = new RestTemplate();

		String url = "https://open-api.kakaopay.com/online/v1/payment/subscription";

		PaymentAproveResponseDTO response = template.postForObject(url, requestEntity, PaymentAproveResponseDTO.class);

		LocalDateTime localDateTime = LocalDateTime.parse(response.getApproved_at());
		Timestamp timestamp = Timestamp.valueOf(localDateTime);

		// 결제 테이블에 save
		Payment payment = Payment.builder()
			.user(user)
			.date(timestamp)
			.amount(Long.valueOf(response.getAmount().getTotal()))
			.description(response.getItem_name())
			.build();

		if (response.getAmount().getTotal() == POINT_AUTO_PRICE.intValue()) {
			Point point = Point.builder()
				.user(user)
				.usedAt(timestamp)
				.balance(POINT_AUTO_PRICE)
				.description(POINT_AUTO_PRICE + " 포인트 자동 충전")
				.build();

			pointRepository.save(point);
		}

		paymentRepository.save(payment);

		return response;
	}

	public PaymentInactiveResponseDTO kakaoPayInactive(Long userId) {
		Sid sid = sidRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		Map<String, String> parameters = new HashMap<>();

		parameters.put("cid", kakaoPayProperties.getSubscriptionCid());
		parameters.put("sid", sid.getSid());

		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

		RestTemplate template = new RestTemplate();

		String url = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive";

		sidRepository.delete(sid);

		return template.postForObject(url, requestEntity, PaymentInactiveResponseDTO.class);
	}
}
