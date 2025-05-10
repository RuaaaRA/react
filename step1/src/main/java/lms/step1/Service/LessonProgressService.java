package lms.step1.Service;

import java.util.List;
import lms.step1.DTO.LessonProgressDTO;

public interface LessonProgressService {
    void markLessonComplete(LessonProgressDTO dto);

    List<LessonProgressDTO> getProgressByCourse(Long courseId);
    List<LessonProgressDTO> getProgressByStudent(String username);

}
