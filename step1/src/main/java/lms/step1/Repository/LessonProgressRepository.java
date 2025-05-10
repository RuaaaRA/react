package lms.step1.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import lms.step1.Model.LessonProgress;
import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByStudentUsernameAndLessonId(String username, Long lessonId);

    List<LessonProgress> findByLesson_CourseId(Long courseId);
    List<LessonProgress> findByStudentUsername(String username);

}
