package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.learnedge.dto.CourseDto;
import pl.learnedge.mapper.CourseMapper;
import pl.learnedge.repository.CourseRepository;
import pl.learnedge.repository.UserCourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final UserCourseRepository userCourseRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

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





}
