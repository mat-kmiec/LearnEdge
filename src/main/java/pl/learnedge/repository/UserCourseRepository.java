package pl.learnedge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.learnedge.model.UserCourse;

import java.util.List;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findAllByUserId(Long userId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

}
