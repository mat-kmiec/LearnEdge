package pl.learnedge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home/index";
    }

//    @GetMapping("/logowanie")
//    public String login() {
//        return "home/login";
//    }

    @GetMapping("/przypomnij-haslo")
    public String forgotPassword() {
        return "home/forgot-password";
    }

//    @GetMapping("/rejestracja")
//    public String register() {
//        return "home/register";
//    }


}
