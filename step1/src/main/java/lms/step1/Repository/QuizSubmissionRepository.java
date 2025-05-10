package lms.step1.Repository;

import lms.step1.Model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
    Optional<QuizSubmission> findByStudentIdAndQuizId(Long studentId, Long quizId);
    List<QuizSubmission> findByQuizId(Long quizId);
}