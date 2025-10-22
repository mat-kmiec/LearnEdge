package pl.learnedge.mapper;

import org.springframework.stereotype.Component;
import pl.learnedge.dto.CourseDto;
import pl.learnedge.model.Course;

@Component
public class CourseMapper {
    public CourseDto toDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setDifficulty(course.getDifficulty());
        return courseDto;
    }
    public Course toEntity(CourseDto courseDto) {
        Course course = new Course();
        course.setId(courseDto.getId());
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setDifficulty(courseDto.getDifficulty());
        return course;
    }
}
