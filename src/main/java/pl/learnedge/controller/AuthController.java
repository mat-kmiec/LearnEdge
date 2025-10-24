package pl.learnedge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.learnedge.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/logowanie")
    public String login() {
        return "home/login";
    }

    @GetMapping("/rejestracja")
    public String registerForm(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        model.addAttribute("error", error);
        return "home/register";
    }

    @PostMapping("/rejestracja")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam(required = false) String email,
                                 @RequestParam String password) {
        try {
            userService.register(username, email, password);
            return "redirect:/logowanie?registered";
        } catch (IllegalArgumentException ex) {
            return "redirect:/rejestracja?error=" + ex.getMessage().replace(" ", "%20");
        }
    }

    // Metody związane z resetowaniem hasła przeniesione do PasswordResetController

}