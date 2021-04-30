package me.isunghan.loginspring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping(value = "/")
    public String loginPage() {
        return "login/login.html";
    }

    @GetMapping(value = "/join")
    public String joinPage() {
        return "login/join.html";
    }

    @GetMapping(value = "/find")
    public String findPage() {
        return "/login/findUser.html";
    }
}
