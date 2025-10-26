package pl.learnedge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.learnedge.model.Lesson;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByCourseId(Long courseId);
}
