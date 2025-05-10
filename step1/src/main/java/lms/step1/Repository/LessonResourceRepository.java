package lms.step1.Repository;

import lms.step1.Model.LessonResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonResourceRepository extends JpaRepository<LessonResource, Long> {
    List<LessonResource> findByLessonId(Long lessonId);

    void deleteById(Long resourceId);
}
