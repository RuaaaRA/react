package lms.step1.Service;

import lms.step1.DTO.LessonDTO;
import lms.step1.DTO.LessonResourceDTO;

import java.util.List;
import java.util.Optional;

public interface LessonService {
    LessonDTO createLesson(LessonDTO dto);
    boolean isCourseExists(Long courseId);
    boolean isLessonExists(Long lessonId);
    List<LessonDTO> getLessonsByCourseId(Long courseId);
    Optional<LessonDTO> getLessonById(Long lessonId); // أضفنا هذه الطريقة
    LessonDTO updateLesson(Long lessonId, LessonDTO dto);
    void deleteLesson(Long lessonId);
   List<LessonResourceDTO> getResourcesByLessonId(Long lessonId);
}