package lms.step1.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import lms.step1.Model.Lesson;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseId(Long courseId);

    default boolean isLessonExists(Long lessonId) {
        return existsById(lessonId);
    }
}
