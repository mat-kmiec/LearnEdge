package pl.learnedge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/panel")
    public String dashboard() {
        return "dashboard/dashboard";
    }
}
