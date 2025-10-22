package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.learnedge.dto.CourseDto;
import pl.learnedge.mapper.CourseMapper;
import pl.learnedge.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public List<CourseDto> getAvailableCoursesForUser(Long userId){
        return courseRepository.findAllCoursesNotEnrolledByUser(userId)
                .stream()
                .map(courseMapper::toDto)
                .toList();
   }




}
