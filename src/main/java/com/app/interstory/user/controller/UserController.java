package com.app.interstory.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("auth")
@RequiredArgsConstructor
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


}
