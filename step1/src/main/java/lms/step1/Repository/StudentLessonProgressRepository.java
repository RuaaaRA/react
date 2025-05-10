package lms.step1.Repository;

import lms.step1.Model.StudentLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentLessonProgressRepository extends JpaRepository<StudentLessonProgress, Long> {

    List<StudentLessonProgress> findByStudentIdAndLesson_CourseId(Long studentId, Long courseId);
}
