package io.github.bodzisz.controller;

import io.github.bodzisz.enitity.User;
import io.github.bodzisz.enitity.dto.UserWriteModel;
import io.github.bodzisz.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userModel", new UserWriteModel());
        return "register-form";
    }

    @PostMapping("/register")
    public String registerTheUser(Model model, @ModelAttribute("userModel") @Valid UserWriteModel userWriteModel,
                                  BindingResult result) {
        if(result.hasErrors()) {
            return "register-form";
        }

        if(!userWriteModel.getPassword().equals(userWriteModel.getConfirmedPassword())) {
            result.addError(new FieldError("userModel", "confirmedPassword",
                    "Given passwords do not match"));
            return "register-form";
        }

        if(userService.existsByUsername(userWriteModel.getUsername())) {
            result.addError(new FieldError("userModel", "username",
                    "Given username already taken"));
            return "register-form";
        }

        userService.save(new User(userWriteModel));
        model.addAttribute("registredUser", "Registration was successfull!");
        return "login-form";
    }
}
