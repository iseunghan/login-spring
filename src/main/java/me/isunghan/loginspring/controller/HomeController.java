package me.isunghan.loginspring.controller;

import me.isunghan.loginspring.security.SessionMemer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    private HttpSession httpSession;

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

    @GetMapping("/login-success")
    public String login_success_handler(SessionMemer member, Model model) {
        // httpSession에 저장된 user의 정보를 가져옵니다.
        member = (SessionMemer) httpSession.getAttribute("member");

        model.addAttribute("name", member.getName());
        model.addAttribute("image", member.getPicture());

        return "login/login-success";
    }
}
