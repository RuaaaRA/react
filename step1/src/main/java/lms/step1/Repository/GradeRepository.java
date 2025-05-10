// GradeRepository.java
package lms.step1.Repository;

import lms.step1.Model.Grade;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {


    Optional<Grade> findBySubmissionId(Long submissionId);
}