// QuestionRepository.java
package lms.step1.Repository;

import lms.step1.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteByQuizId(Long quizId);
    
}