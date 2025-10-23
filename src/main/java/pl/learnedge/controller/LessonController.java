package pl.learnedge.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LessonController {

    @GetMapping("/lekcja")
    public String lesson() {
        return "course/lesson";
    }

    @GetMapping("/kreator-lekcji")
    public String lessonCreator() {
        return "course/create-lesson";
    }
}
