package pl.learnedge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.learnedge.dto.LessonDto;
import pl.learnedge.mapper.CourseMapper;
import pl.learnedge.service.LessonService;

@Controller
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final CourseMapper courseMapper;

    @GetMapping("/kurs/{course_slug}/{lesson_slug}")
    public String lesson(@PathVariable String course_slug, @PathVariable String lesson_slug, Model model) {
        LessonDto lesson = lessonService.getLessonBySlug(lesson_slug);
        model.addAttribute("lesson", lesson);
        return "course/lesson";
    }

    @GetMapping("/kreator-lekcji/{course_id}/{course_name}")
    public String lessonCreator(@PathVariable("course_id") Long courseId,
                                @PathVariable("course_name") String courseName,
                                Model model) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseName", courseName);
        return "course/create-lesson";
    }
}


