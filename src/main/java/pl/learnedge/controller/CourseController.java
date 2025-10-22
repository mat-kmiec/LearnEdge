package pl.learnedge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {

    @GetMapping("/dostepne-kursy")
    public String course() {
        return "course/course";
    }


}
