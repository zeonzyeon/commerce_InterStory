package com.app.interstory.user.controller;

import com.app.interstory.user.dto.request.LocalLoginRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("auth")
public class UserController {

    //로그인 페이지
    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    //회원 가입 페이지
    @GetMapping("/signup")
    public String signup() {
        return "user/signup";
    }

    //로그인 요청
    @PostMapping("/signup")
    public String signup(@ModelAttribute LocalLoginRequest loginRequest) {

        return "/";
    }


}
