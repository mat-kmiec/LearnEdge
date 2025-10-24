package pl.learnedge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.learnedge.model.Course;
import pl.learnedge.model.UserCourse;

import java.util.Collection;
import java.util.List;

public interface CourseRepository extends JpaRepository<UserCourse, Long> {

    @Query("""
    SELECT c FROM Course c
    WHERE c.id NOT IN (
        SELECT uc.course.id FROM UserCourse uc WHERE uc.user.id = :userId
    )
""")
    List<Course> findAllCoursesNotEnrolledByUser(@Param("userId") Long userId);

}
