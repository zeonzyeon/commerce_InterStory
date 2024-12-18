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
import com.app.interstory.payment.domain.entity.Subscribe;
import com.app.interstory.payment.domain.enumtypes.PaymentStatus;
import com.app.interstory.payment.domain.enumtypes.PaymentType;
import com.app.interstory.payment.domain.repository.PaymentRepository;
import com.app.interstory.payment.domain.repository.SidRepository;
import com.app.interstory.payment.domain.repository.SubscribeRepository;
import com.app.interstory.payment.dto.response.PaymentAproveResponseDTO;
import com.app.interstory.payment.dto.response.PaymentInactiveResponseDTO;
import com.app.interstory.payment.dto.response.PaymentReadyResponseDTO;
import com.app.interstory.payment.properties.KakaoPayProperties;
import com.app.interstory.user.domain.entity.Coupon;
import com.app.interstory.user.domain.entity.Point;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.domain.entity.UserCoupon;
import com.app.interstory.user.repository.CouponRepository;
import com.app.interstory.user.repository.PointRepository;
import com.app.interstory.user.repository.UserCouponRepository;
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
	private static final Long POINT_PRICE_SECOND = 3_000L;
	private static final Long POINT_PRICE_THIRD = 5_000L;
	private static final Long POINT_PRICE_FOURTH = 10_000L;
	private static final Long POINT_PRICE_FIFTH = 50_000L;
	private static final Long POINT_SEQUENCE_PRICE = 9_999L;
	private static final Long SUBSCRIBE_ONCE = 14_999L;
	private static final Long POINT_AUTO_PRICE = 100_000L;
	private final KakaoPayProperties kakaoPayProperties;
	private final SidRepository sidRepository;
	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final PointRepository pointRepository;
	private final CouponRepository couponRepository;
	private final UserCouponRepository userCouponRepository;
	private final SubscribeRepository subscribeRepository;
	private PaymentReadyResponseDTO paymentReadyResponseDTO;
	private Long partnerOrderId;
	private Long orderUserCouponId;

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();

		String auth = "SECRET_KEY " + kakaoPayProperties.getSecretKey();
		headers.set("Authorization", auth);
		headers.set("Content-type", "application/json");

		return headers;
	}

	public PaymentReadyResponseDTO kakaoPayReady(Long userId, PaymentType type, Long userCouponId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		String cid = kakaoPayProperties.getOnetimeCid();
		String name = "포인트 결제";
		Long totalPrice;
		Long discount = 0L;

		if (userCouponId != 0) {
			orderUserCouponId = userCouponId;

			UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
				.orElseThrow(() -> new IllegalArgumentException("해당 유저쿠폰 정보가 없습니다. id : " + userCouponId));
			Long couponId = userCoupon.getCoupon().getCouponId();
			Coupon coupon = couponRepository.findById(couponId)
				.orElseThrow(() -> new IllegalArgumentException("해당 쿠폰 정보가 없습니다. id : " + couponId));

			discount = (long)coupon.getCouponEffect().getDiscountAmount();
		}

		totalPrice = calcTotalPrice(type, discount);

		if (type == PaymentType.SEQUENCE) {
			cid = kakaoPayProperties.getSequenceCid();
			name = "구독 정기 결제";
		} else if (type == PaymentType.AUTO) {
			cid = kakaoPayProperties.getSubscriptionCid();
			name = "포인트 자동 결제";
		} else if (type == PaymentType.SUB_ONCE) {
			name = "구독 1회 결제";
		}

		Payment pendingPayment = Payment.builder()
			.user(user)
			.date(Timestamp.valueOf(LocalDateTime.now()))
			.amount(totalPrice)
			.description(name)
			.status(PaymentStatus.PENDING)
			.build();

		Payment savedPayment = paymentRepository.save(pendingPayment);

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("cid", cid);                                                        // 가맹점 코드(테스트용)
		parameters.put("partner_order_id", savedPayment.getPaymentId().toString());        // 주문번호(테이블과 매칭 필요)
		parameters.put("partner_user_id", userId.toString());                              // 회원 아이디
		parameters.put("item_name", name);                                                 // 상품명
		parameters.put("quantity", "1");                                                   // 상품 수량
		parameters.put("total_amount", totalPrice.toString());                             // 상품 총액
		parameters.put("tax_free_amount", "0");                                            // 상품 비과세 금액
		parameters.put("approval_url", "http://localhost:8080/api/cash/request");          // 결제 성공 시 URL
		parameters.put("cancel_url", "http://localhost:8080/api/cash/cancel");             // 결제 취소 시 URL
		parameters.put("fail_url", "http://localhost:8080/api/cash/fail");                 // 결제 실패 시 URL

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

		RestTemplate template = new RestTemplate();

		String url = "https://open-api.kakaopay.com/online/v1/payment/ready";

		paymentReadyResponseDTO = template.postForObject(url, requestEntity, PaymentReadyResponseDTO.class);
		partnerOrderId = savedPayment.getPaymentId();

		return paymentReadyResponseDTO;
	}

	public PaymentAproveResponseDTO kakaoPayApprove(Long userId, String pgToken) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));
		Payment payment = paymentRepository.findById(partnerOrderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 결제 정보가 없습니다. id : " + partnerOrderId));

		Map<String, String> parameters = new HashMap<>();

		parameters.put("cid", kakaoPayProperties.getOnetimeCid());
		parameters.put("tid", paymentReadyResponseDTO.getTid());
		parameters.put("partner_order_id", payment.getPaymentId().toString());
		parameters.put("partner_user_id", userId.toString());
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

		payment.updateDate(timestamp);
		payment.updateStatus(PaymentStatus.COMPLETED);

		Integer discount = 0;

		if (orderUserCouponId != null) {
			UserCoupon userCoupon = userCouponRepository.findById(orderUserCouponId)
				.orElseThrow(() -> new IllegalArgumentException("해당 유저쿠폰 정보가 없습니다. id : " + orderUserCouponId));
			Coupon coupon = couponRepository.findById(userCoupon.getCoupon().getCouponId())
				.orElseThrow(() -> new IllegalArgumentException("해당 쿠폰 정보가 없습니다. id : " + userCoupon.getCoupon().getCouponId()));

			discount = coupon.getCouponEffect().getDiscountAmount();
			userCouponRepository.delete(userCoupon);
		}

		Integer amount = response.getAmount().getTotal();
		int totalAmount = amount + discount;
		if (totalAmount == POINT_AUTO_PRICE.intValue()) {
			Point point = Point.builder()
				.user(user)
				.usedAt(timestamp)
				.balance(POINT_AUTO_PRICE)
				.description((POINT_AUTO_PRICE.intValue() / 20) + " 포인트 자동 충전")
				.build();

			pointRepository.save(point);

			user.updateIsAutoPayment(true);

			user.updatePoint(user.getPoint() + (POINT_AUTO_PRICE.intValue() / 20));
		} else if (totalAmount == POINT_SEQUENCE_PRICE.intValue()) {
			user.updateIsSubscribe(true);

			Subscribe subscribe = subscribeRepository.findByUser_UserId(userId);
			if (subscribe == null) {
				subscribe = Subscribe.builder()
					.user(user)
					.endAt(Timestamp.valueOf(LocalDateTime.now().plusMonths(1)))
					.build();
				subscribeRepository.save(subscribe);
			} else {
				subscribe.updateEndAt(Timestamp.valueOf(subscribe.getEndAt().toLocalDateTime().plusMonths(1)));
			}
		} else {
			Long price;
			int pointPriceFirst = POINT_PRICE_FIRST.intValue();
			int pointPriceSecond = POINT_PRICE_SECOND.intValue();
			int pointPriceThird = POINT_PRICE_THIRD.intValue();
			int pointPriceFourth = POINT_PRICE_FOURTH.intValue();
			int pointPriceFifth = POINT_PRICE_FIFTH.intValue();

			price = switch (totalAmount) {
				case 1_000 -> (long)(pointPriceFirst);
				case 3_000 -> (long)(pointPriceSecond);
				case 5_000 -> (long)(pointPriceThird);
				case 10_000 -> (long)(pointPriceFourth);
				default -> (long)(pointPriceFifth);
			};

			Point point = Point.builder()
				.user(user)
				.usedAt(timestamp)
				.balance(price)
				.description((price / 20) + " 포인트 충전")
				.build();

			pointRepository.save(point);

			user.updatePoint(user.getPoint() + (price / 20));
		}

		paymentRepository.save(payment);

		return response;
	}

	public PaymentAproveResponseDTO kakaoPayPayment(Long userId, PaymentType type) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));
		Sid sid = sidRepository.findByUser_UserId(userId);
		if (sid == null) {
			throw new IllegalArgumentException(NOT_FOUND_USER_ID + userId);
		}

		String name;
		Long totalPrice = calcTotalPrice(type, 0L);
		if (type == PaymentType.SEQUENCE) {
			name = "포인트 정기 결제";
		} else {
			name = "포인트 자동 결제";
		}

		Payment pendingPayment = Payment.builder()
			.user(user)
			.date(Timestamp.valueOf(LocalDateTime.now()))
			.amount(totalPrice)
			.description(name)
			.status(PaymentStatus.PENDING)
			.build();

		Payment savedPayment = paymentRepository.save(pendingPayment);

		Map<String, String> parameters = new HashMap<>();

		parameters.put("cid", kakaoPayProperties.getSubscriptionCid());
		parameters.put("sid", sid.getSid());
		parameters.put("partner_order_id", savedPayment.getPaymentId().toString());
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

		savedPayment.updateStatus(PaymentStatus.COMPLETED);
		savedPayment.updateDate(timestamp);

		if (type == PaymentType.SEQUENCE) {
			user.updateIsSubscribe(true);

			Subscribe subscribe = subscribeRepository.findByUser_UserId(userId);
			if (subscribe == null) {
				subscribe = Subscribe.builder()
					.user(user)
					.endAt(Timestamp.valueOf(LocalDateTime.now().plusMonths(1)))
					.build();
				subscribeRepository.save(subscribe);
			} else {
				subscribe.updateEndAt(Timestamp.valueOf(subscribe.getEndAt().toLocalDateTime().plusMonths(1)));
			}
		} else if (type == PaymentType.AUTO) {
			user.updateIsAutoPayment(true);
		}

		if (response.getAmount().getTotal() == POINT_AUTO_PRICE.intValue()) {
			Point point = Point.builder()
				.user(user)
				.usedAt(timestamp)
				.balance(POINT_AUTO_PRICE)
				.description((POINT_AUTO_PRICE.intValue() / 20) + " 포인트 자동 충전")
				.build();

			pointRepository.save(point);

			user.updatePoint(user.getPoint() + (POINT_AUTO_PRICE / 20));
		}

		return response;
	}

	public PaymentInactiveResponseDTO kakaoPayInactiveSubscription(Long userId) {
		Sid sid = sidRepository.findByUser_UserId(userId);
		if (sid == null) {
			throw new IllegalArgumentException(NOT_FOUND_USER_ID + userId);
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		if (!user.getIsAutoPayment()) {
			Map<String, String> parameters = new HashMap<>();

			parameters.put("cid", kakaoPayProperties.getSubscriptionCid());
			parameters.put("sid", sid.getSid());

			HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

			RestTemplate template = new RestTemplate();

			String url = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive";

			sidRepository.delete(sid);
			user.updateIsSubscribe(false);

			return template.postForObject(url, requestEntity, PaymentInactiveResponseDTO.class);
		}

		user.updateIsSubscribe(false);

		return new PaymentInactiveResponseDTO();
	}

	public PaymentInactiveResponseDTO kakaoPayInactiveAutoPayment(Long userId) {
		Sid sid = sidRepository.findByUser_UserId(userId);
		if (sid == null) {
			throw new IllegalArgumentException(NOT_FOUND_USER_ID + userId);
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		if (!user.getIsSubscribe()) {
			Map<String, String> parameters = new HashMap<>();

			parameters.put("cid", kakaoPayProperties.getSubscriptionCid());
			parameters.put("sid", sid.getSid());

			HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

			RestTemplate template = new RestTemplate();

			String url = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive";

			sidRepository.delete(sid);
			user.updateIsAutoPayment(false);

			return template.postForObject(url, requestEntity, PaymentInactiveResponseDTO.class);
		}

		user.updateIsAutoPayment(false);

		return new PaymentInactiveResponseDTO();
	}

	public String kakaoPayCancel() {
		Payment payment = paymentRepository.findById(partnerOrderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 결제 정보가 없습니다. id : " + partnerOrderId));

		payment.updateStatus(PaymentStatus.CANCELED);

		return "결제가 취소되었습니다";
	}

	public String kakaoPayFail() {
		Payment payment = paymentRepository.findById(partnerOrderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 결제 정보가 없습니다. id : " + partnerOrderId));

		payment.updateStatus(PaymentStatus.FAILED);

		return "결제가 실패하였습니다";
	}

	public PaymentReadyResponseDTO kakaoPayChangePayment(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ID + userId));

		Sid sid = sidRepository.findByUser_UserId(userId);
		if (sid != null)
			sidRepository.delete(sid);

		String cid = kakaoPayProperties.getSequenceCid();
		String name = "결제 수단 변경";
		Long totalPrice = 0L;

		Payment pendingPayment = Payment.builder()
			.user(user)
			.date(Timestamp.valueOf(LocalDateTime.now()))
			.amount(totalPrice)
			.description(name)
			.status(PaymentStatus.PENDING)
			.build();

		Payment savedPayment = paymentRepository.save(pendingPayment);

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("cid", cid);                                                        // 가맹점 코드(테스트용)
		parameters.put("partner_order_id", savedPayment.getPaymentId().toString());        // 주문번호(테이블과 매칭 필요)
		parameters.put("partner_user_id", userId.toString());                              // 회원 아이디
		parameters.put("item_name", name);                                                 // 상품명
		parameters.put("quantity", "1");                                                   // 상품 수량
		parameters.put("total_amount", totalPrice.toString());                             // 상품 총액
		parameters.put("tax_free_amount", "0");                                            // 상품 비과세 금액
		parameters.put("approval_url", "http://localhost:8080/api/cash/request");          // 결제 성공 시 URL
		parameters.put("cancel_url", "http://localhost:8080/api/cash/cancel");             // 결제 취소 시 URL
		parameters.put("fail_url", "http://localhost:8080/api/cash/fail");                 // 결제 실패 시 URL

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

		RestTemplate template = new RestTemplate();

		String url = "https://open-api.kakaopay.com/online/v1/payment/ready";

		paymentReadyResponseDTO = template.postForObject(url, requestEntity, PaymentReadyResponseDTO.class);
		partnerOrderId = savedPayment.getPaymentId();

		return paymentReadyResponseDTO;
	}

	public Boolean checkSid(Long userId) {
		return sidRepository.existsByUser_UserId(userId);
	}

	private Long calcTotalPrice(PaymentType type, Long discount) {
		return switch (type) {
			case NORMAL_FIRST -> POINT_PRICE_FIRST - discount;
			case NORMAL_SECOND -> POINT_PRICE_SECOND - discount;
			case NORMAL_THIRD -> POINT_PRICE_THIRD - discount;
			case NORMAL_FOURTH -> POINT_PRICE_FOURTH - discount;
			case NORMAL_FIFTH -> POINT_PRICE_FIFTH - discount;
			case SUB_ONCE -> SUBSCRIBE_ONCE;
			case SEQUENCE -> POINT_SEQUENCE_PRICE;
			case AUTO -> POINT_AUTO_PRICE;
			default -> 0L;
		};
	}
}
