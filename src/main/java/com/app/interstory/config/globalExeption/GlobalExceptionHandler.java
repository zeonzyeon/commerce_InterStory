package com.app.interstory.config.globalExeption;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //커스텀 ex - 에러페이지로 보낼 view 필요
//    @ExceptionHandler(IllegalArgumentException.class)
//    protected ModelAndView illegalArgumentException(
//            IllegalArgumentException e,
//            Model model
//    ) {
//        model.addAttribute("error", new ErrorResponse("400","요청한 자료를 찾을 수 없습니다."));
//        return new ModelAndView("error/error");
//    }


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
