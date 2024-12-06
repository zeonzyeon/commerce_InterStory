package com.app.interstory.user.controller;

import com.app.interstory.user.domain.CustomUserDetails;
import com.app.interstory.user.dto.response.NoticeListResponseDTO;
import com.app.interstory.user.dto.request.NoticeRequestDTO;
import com.app.interstory.user.dto.response.NoticeResponseDTO;
import com.app.interstory.user.dto.response.UserListResponseDTO;
import com.app.interstory.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<UserListResponseDTO> getUsers(@RequestParam(defaultValue = "1") Integer page, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(adminService.getUsers(page, userDetails));
    }

    @GetMapping("/users/search")
    public ResponseEntity<UserListResponseDTO> searchUsers(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "1") Integer page,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(adminService.searchUsers(nickname, email, page, userDetails));
    }

    @PostMapping("/users/{userId}/active")
    public ResponseEntity<String> activeUser(@PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(adminService.activeUser(userId, userDetails));
    }

    @PostMapping("/notices")
    public ResponseEntity<String> writeNotice(@RequestBody NoticeRequestDTO noticeRequestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(adminService.writeNotice(noticeRequestDTO, userDetails));
    }

    @PutMapping("/notices/{noticeId}")
    public ResponseEntity<String> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequestDTO noticeRequestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return  ResponseEntity.ok(adminService.updateNotice(noticeId, noticeRequestDTO, userDetails));
    }

    @GetMapping("/notices")
    public ResponseEntity<NoticeListResponseDTO> getNoticeList(@RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(adminService.getNoticeList(page));
    }

    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> getNoticeDetail(@PathVariable Long noticeId) {
        return ResponseEntity.ok(adminService.getNoticeDetail(noticeId));
    }

    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.deleteNotice(noticeId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
