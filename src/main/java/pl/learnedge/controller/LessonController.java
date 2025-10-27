package pl.learnedge.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.learnedge.dto.CourseDto;
import pl.learnedge.dto.LessonDto;
import pl.learnedge.service.CourseService;
import pl.learnedge.service.LessonService;

@Controller
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/kurs/{course_slug}/{lesson_slug}")
    public String lesson(@PathVariable String course_slug, @PathVariable String lesson_slug, Model model) {
        LessonDto lesson = lessonService.getLessonBySlug(lesson_slug);
        model.addAttribute("lesson", lesson);
        return "course/lesson";
    }

    @GetMapping("/kreator-lekcji")
    public String lessonCreator() {
        return "course/create-lesson";
    }
}


