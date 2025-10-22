package pl.learnedge.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.learnedge.dto.CourseDto;
import pl.learnedge.model.Course;
import pl.learnedge.model.User;
import pl.learnedge.service.AuthService;
import pl.learnedge.service.CourseService;

import java.util.List;

@Controller
@AllArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final AuthService authService;

    @GetMapping("/dostepne-kursy")
    public String course(Model model, @AuthenticationPrincipal User user) {
        long userId = authService.getCurrentUserId();
        List<CourseDto> courses = courseService.getAvailableCoursesForUser(userId);
        model.addAttribute("courses", courses);
        return "dashboard/available-courses";
    }


}
