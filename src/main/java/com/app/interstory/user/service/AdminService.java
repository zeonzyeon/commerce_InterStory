package com.app.interstory.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.domain.entity.Coupon;
import com.app.interstory.user.domain.entity.Notice;
import com.app.interstory.user.domain.entity.User;
import com.app.interstory.user.domain.enumtypes.Roles;
import com.app.interstory.user.dto.request.CouponRequestDTO;
import com.app.interstory.user.dto.request.NoticeRequestDTO;
import com.app.interstory.user.dto.response.CouponListResponseDTO;
import com.app.interstory.user.dto.response.CouponResponseDTO;
import com.app.interstory.user.dto.response.NoticeListResponseDTO;
import com.app.interstory.user.dto.response.NoticeResponseDTO;
import com.app.interstory.user.dto.response.UserListResponseDTO;
import com.app.interstory.user.dto.response.UserResponseDTO;
import com.app.interstory.user.repository.CouponRepository;
import com.app.interstory.user.repository.NoticeRepository;
import com.app.interstory.user.repository.UserRepository;
import com.app.interstory.util.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final NoticeRepository noticeRepository;
	private final CouponRepository couponRepository;

	@Transactional
	public UserListResponseDTO getUsers(@RequestParam(defaultValue = "1") Integer page, CustomUserDetails userDetails) {
		final int getUserCount = 10;

		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("유저 조회 권한이 없습니다.");
		}

		Pageable pageable = PageRequest.of(page - 1, getUserCount);

		Page<User> users = userRepository.findByRoleNot(userDetails.getUser().getRole(), pageable);

		if (page > users.getTotalPages()) {
			throw new RuntimeException("유효하지 않은 페이지입니다.");
		}

		List<UserResponseDTO> userList = users.stream()
			.map(UserResponseDTO::from)
			.toList();

		return UserListResponseDTO.from(userList, users.getTotalPages());
	}

	@Transactional
	public String activeUser(Long userId, CustomUserDetails userDetails) {
		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("유저 활성화 권한이 없습니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("해당 ID의 유저가 없습니다."));

		user.active();

		userRepository.save(user);

		return "유저 활성화가 완료되었습니다.";
	}

	@Transactional
	public UserListResponseDTO searchUsers(String nickname, String email, Integer page, CustomUserDetails userDetails) {
		final int getUserCount = 10;

		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("유저 검색 권한이 없습니다.");
		}

		Pageable pageable = PageRequest.of(page - 1, getUserCount);

		Page<User> users = userRepository.searchUsers(nickname, email, pageable);

		List<UserResponseDTO> userList = users.stream()
			.map(UserResponseDTO::from)
			.toList();

		return UserListResponseDTO.from(userList, users.getTotalPages());
	}

	@Transactional
	public String writeNotice(NoticeRequestDTO noticeRequestDTO, CustomUserDetails userDetails) {

		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("공지사항 작성 권한이 없습니다.");
		}

		User user = userRepository.findById(userDetails.getUser().getUserId())
			.orElseThrow(() -> new RuntimeException("유효하지 않은 사용자입니다."));

		Notice notice = Notice.builder()
			.user(user)
			.title(noticeRequestDTO.getTitle())
			.content(noticeRequestDTO.getContent())
			.build();
		noticeRepository.save(notice);

		return "공지사항 작성이 완료되었습니다.";
	}

	@Transactional
	public String updateNotice(Long noticeId, NoticeRequestDTO noticeRequestDTO, CustomUserDetails userDetails) {
		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("공지사항 수정 권한이 없습니다.");
		}

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new RuntimeException("해당 ID의 공지사항이 없습니다."));

		notice.update(noticeRequestDTO.getTitle(), noticeRequestDTO.getContent());

		return "공지사항 수정이 완료되었습니다.";
	}

	@Transactional
	public NoticeListResponseDTO getNoticeList(Integer page) {
		final int getItemCount = 10;

		Pageable pageable = PageRequest.of(page - 1, getItemCount, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<Notice> notices = noticeRepository.findAll(pageable);

		if (page > notices.getTotalPages()) {
			throw new RuntimeException("유효하지 않은 페이지입니다.");
		}

		List<NoticeResponseDTO> noticeResponseDtos = notices.stream().map(NoticeResponseDTO::from).toList();

		return NoticeListResponseDTO.from(noticeResponseDtos, notices.getTotalPages());
	}

	@Transactional
	public NoticeResponseDTO getNoticeDetail(Long noticeId) {
		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new RuntimeException("해당 Id의 공지사항이 없습니다."));

		return NoticeResponseDTO.from(notice);
	}

	@Transactional
	public void deleteNotice(Long noticeId, CustomUserDetails userDetails) {
		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("공지사항 삭제 권한이 없습니다.");
		}

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new RuntimeException("해당 Id의 공지사항이 없습니다."));

		noticeRepository.delete(notice);
	}

	@Transactional
	public CouponResponseDTO generateCoupon(CouponRequestDTO couponRequestDTO, CustomUserDetails userDetails) {
		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("쿠폰 생성 권한이 없습니다.");
		}

		Coupon coupon = Coupon.builder()
			.expiredAt(couponRequestDTO.getExpiredAt())
			.name(couponRequestDTO.getName())
			.code(Utils.getRenameNickname(""))
			.couponEffect(couponRequestDTO.getEffect())
			.build();

		couponRepository.save(coupon);

		return CouponResponseDTO.from(coupon);
	}

	public CouponListResponseDTO getCoupons(Integer page, CustomUserDetails userDetails) {
		final int getItemCount = 10;

		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("쿠폰 전체 조회 권한이 없습니다.");
		}

		Pageable pageable = PageRequest.of(page - 1, getItemCount, Sort.by(Sort.Direction.DESC, "expiredAt"));

		Page<Coupon> coupons = couponRepository.findAll(pageable);

		if (page > coupons.getTotalPages()) {
			throw new RuntimeException("유효하지 않은 페이지입니다.");
		}

		List<CouponResponseDTO> couponResponseDTOS = coupons.stream().map(CouponResponseDTO::from).toList();

		return CouponListResponseDTO.from(couponResponseDTOS, coupons.getTotalPages());
	}

	public void deleteCoupon(Long couponId, CustomUserDetails userDetails) {
		if (userDetails == null || userDetails.getUser().getRole() != Roles.ADMIN) {
			throw new IllegalStateException("쿠폰 삭제 권한이 없습니다.");
		}

		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new RuntimeException("해당 Id의 쿠폰이 없습니다."));

		couponRepository.delete(coupon);
	}
}