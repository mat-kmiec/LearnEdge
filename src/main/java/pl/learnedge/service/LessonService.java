package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.learnedge.dto.LessonDto;
import pl.learnedge.exception.LessonNotFoundException;
import pl.learnedge.mapper.LessonMapper;
import pl.learnedge.model.Lesson;
import pl.learnedge.repository.LessonRepository;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;


    public LessonDto getLessonBySlug(String slug){
        Lesson lesson = lessonRepository.findBySlug(slug).orElseThrow(LessonNotFoundException::new);
        return lessonMapper.toDto(lesson);
    }
}
