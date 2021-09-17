package io.github.bodzisz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login-form";
    }

    @GetMapping("/logout")
    public String logout() {
        return "login-form";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        return "login-form";
    }

}
