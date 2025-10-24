package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.learnedge.dto.CourseDto;
import pl.learnedge.dto.LessonDto;
import pl.learnedge.exception.CourseNotFoundException;
import pl.learnedge.mapper.CourseMapper;
import pl.learnedge.mapper.LessonMapper;
import pl.learnedge.model.Course;
import pl.learnedge.model.Lesson;
import pl.learnedge.repository.CourseRepository;
import pl.learnedge.repository.LessonRepository;
import pl.learnedge.repository.UserCourseRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final UserCourseRepository userCourseRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public List<CourseDto> getAvailableCoursesForUser(Long userId){
        return courseRepository.findAllCoursesNotEnrolledByUser(userId)
                .stream()
                .map(courseMapper::toDto)
                .toList();
   }

   public List<CourseDto> getEnrolledCoursesForUser(Long userId){
        return userCourseRepository.findAllByUserId(userId)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Transactional
    public CourseDto getCourseBySlug(String slug){
        Course course = courseRepository.findBySlug(slug).orElseThrow(CourseNotFoundException::new);
        List<LessonDto> lessons = lessonRepository.findAllByCourseId(course.getId())
                .stream()
                .map(lessonMapper::toDto)
                .toList();
        return courseMapper.toDto(course, lessons);
    }





}
