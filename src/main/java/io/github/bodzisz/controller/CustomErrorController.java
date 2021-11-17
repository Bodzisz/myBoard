package io.github.bodzisz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "access-denied";
    }
}
