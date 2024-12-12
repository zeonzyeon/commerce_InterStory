package com.app.interstory.user.controller;

import com.app.interstory.novel.domain.enumtypes.NovelStatus;
import com.app.interstory.novel.domain.enumtypes.Sort;
import com.app.interstory.novel.dto.request.NovelSearchRequestDTO;
import com.app.interstory.novel.dto.response.NovelDetailResponseDTO;
import com.app.interstory.novel.dto.response.NovelResponseDTO;
import com.app.interstory.novel.service.NovelService;
import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.response.CouponListResponseDTO;
import com.app.interstory.user.dto.response.NoticeListResponseDTO;
import com.app.interstory.user.dto.response.NoticeResponseDTO;
import com.app.interstory.user.dto.response.UserListResponseDTO;
import com.app.interstory.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final NovelService novelService;

    @GetMapping("/notices")
    public String getNoticeList(
            @RequestParam(defaultValue = "1") Integer page,
            Model model) {

        NoticeListResponseDTO noticeList = adminService.getNoticeList(page);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("currentPage", page);

        return "admin/admin-notice-list";
    }

    // 공지사항 작성 페이지
    @GetMapping("/notice/write")
    public String getNoticeWritePage() {
        return "admin/admin-notice-write";
    }

    // 공지사항 상세 페이지
    @GetMapping("/notices/{noticeId}")
    public String getNoticeDetail(@PathVariable Long noticeId, Model model) {
        NoticeResponseDTO notice = adminService.getNoticeDetail(noticeId);
        model.addAttribute("notice", notice);
        return "admin/admin-notice-detail";
    }

    // 공지사항 수정 페이지
    @GetMapping("/notices/{noticeId}/edit")
    public String getNoticeEditPage(@PathVariable Long noticeId, Model model) {
        NoticeResponseDTO notice = adminService.getNoticeDetail(noticeId);
        model.addAttribute("notice", notice);
        return "admin/admin-notice-edit";
    }

    // 쿠폰 목록 페이지
    @GetMapping("/coupons")
    public String getCouponList(
            @RequestParam(defaultValue = "1") Integer page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        try {
            CouponListResponseDTO couponList = adminService.getCoupons(page, userDetails);
            model.addAttribute("couponList", couponList);
            model.addAttribute("currentPage", page);
            return "admin/admin-coupon-list";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error"; // 권한 없음 에러 페이지로 리다이렉트
        }
    }

    // 유저 목록 페이지
    @GetMapping("/users")
    public String getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        try {
            UserListResponseDTO userList = adminService.getUsers(page, userDetails);
            model.addAttribute("userList", userList);
            model.addAttribute("currentPage", page);
            return "admin/admin-user-list";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    // 유저 검색
    @GetMapping("/users/search")
    public String searchUsers(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "1") Integer page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        try {
            UserListResponseDTO userList = adminService.searchUsers(nickname, email, page, userDetails);
            model.addAttribute("userList", userList);
            model.addAttribute("currentPage", page);
            model.addAttribute("searchNickname", nickname);
            model.addAttribute("searchEmail", email);
            return "admin/admin-user-list";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/plans")
    public String getPlanList(
            @RequestParam(defaultValue = "1") Integer page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        try {
            Page<NovelResponseDTO> plans = novelService.getNovelList(
                    NovelStatus.DRAFT,
                    null, null, null, null,
                    Sort.NEW_TO_OLD,
                    PageRequest.of(page - 1, 10)
            );

            model.addAttribute("planList", plans);
            model.addAttribute("currentPage", page);
            return "admin/admin-plan-list";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    // 기획서 상세 조회
    @GetMapping("/plans/{novelId}")
    public String getPlanDetail(
            @PathVariable Long novelId,
            Model model) {
        try {
            NovelDetailResponseDTO plan = novelService.readNovel(
                    novelId,
                    Sort.NEW_TO_OLD,
                    PageRequest.of(0, 1)
            );
            model.addAttribute("plan", plan);
            return "admin/admin-plan-detail";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    // 에러 핸들링
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
