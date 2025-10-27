package pl.learnedge.mapper;

import org.springframework.stereotype.Component;
import pl.learnedge.dto.LessonDto;
import pl.learnedge.model.Lesson;

@Component
public class LessonMapper {
    public LessonDto toDto(Lesson lesson) {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(lesson.getId());
        lessonDto.setTitle(lesson.getTitle());
        lessonDto.setContent(lesson.getContent());
        lessonDto.setLessonOrder(lesson.getLessonOrder());
        lessonDto.setCourse(lesson.getCourse());
        lessonDto.setSlug(lesson.getSlug());
        return lessonDto;
    }

    public Lesson toEntity(LessonDto lessonDto) {
        Lesson lesson = new Lesson();
        lesson.setId(lessonDto.getId());
        lesson.setTitle(lessonDto.getTitle());
        lesson.setContent(lessonDto.getContent());
        lesson.setLessonOrder(lessonDto.getLessonOrder());
        lesson.setCourse(lessonDto.getCourse());
        return lesson;
    }
}
