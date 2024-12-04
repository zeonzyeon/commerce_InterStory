package com.app.interstory.config.globalExeption;

import com.app.interstory.config.globalExeption.customException.DuplicateEmailException;
import com.app.interstory.config.globalExeption.customException.DuplicateNicknameException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //view 처리
//    @ExceptionHandler(IllegalArgumentException.class)
//    protected ModelAndView illegalArgumentException(
//            IllegalArgumentException e,
//            Model model
//    ) {
//        model.addAttribute("error", new ErrorResponse("400","요청한 자료를 찾을 수 없습니다."));
//        return new ModelAndView("error/error");
//    }

    // REST API 에러 처리
    @ExceptionHandler({DuplicateEmailException.class, DuplicateNicknameException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDuplicateException(RuntimeException e) {
        log.error("Duplicate error occurred: ", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("409", e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication failed: ", e);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("401", "로그인에 실패했습니다."));
    }

    // 접근 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied: ", e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("403", "접근 권한이 없습니다."));
    }

    // 사용자를 찾을 수 없음
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("User not found: ", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("404", e.getMessage()));
    }


    /// ErrorResponse 클래스
    @Getter
    @Setter
    public static class ErrorResponse {
        // getters
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

    }
}
