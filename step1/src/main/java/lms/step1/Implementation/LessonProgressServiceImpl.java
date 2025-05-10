package lms.step1.Implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lms.step1.DTO.LessonProgressDTO;
import java.util.List;
import java.util.stream.Collectors;
import lms.step1.Repository.LessonProgressRepository;
import lms.step1.Model.Lesson;
import lms.step1.Model.LessonProgress;
import lms.step1.Service.LessonProgressService;
import lms.step1.Repository.LessonRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonProgressServiceImpl implements LessonProgressService {

    private final LessonProgressRepository progressRepository;
    private final LessonRepository lessonRepository;

    @Override
    public void markLessonComplete(LessonProgressDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        log.info("📌 Attempting to mark lesson as complete. Username: {}, Lesson ID: {}", username, dto.getLessonId());

        Lesson lesson = lessonRepository.findById(dto.getLessonId())
                .orElseThrow(() -> {
                    log.warn("❌ Lesson with ID {} not found", dto.getLessonId());
                    return new RuntimeException("Lesson not found");
                });

        LessonProgress progress = progressRepository
                .findByStudentUsernameAndLessonId(username, dto.getLessonId())
                .orElse(new LessonProgress());

        progress.setStudentUsername(username);
        progress.setLesson(lesson);
        progress.setCompleted(true);

        progressRepository.save(progress);
        log.info("✅ Lesson marked as completed for user: {}, lesson: {}", username, lesson.getTitle());
    }

    @Override
    public List<LessonProgressDTO> getProgressByCourse(Long courseId) {
        log.info("📊 Fetching lesson progress for course ID: {}", courseId);

        List<LessonProgress> all = progressRepository.findByLesson_CourseId(courseId);

        List<LessonProgressDTO> results = all.stream().map(p -> LessonProgressDTO.builder()
                .lessonId(p.getLesson().getId())
                .courseId(courseId)
                .lessonTitle(p.getLesson().getTitle())
                .studentUsername(p.getStudentUsername())
                .completed(p.isCompleted())
                .build()).collect(Collectors.toList());

        log.info("✅ Retrieved {} progress records for course ID: {}", results.size(), courseId);
        return results;
    }

    @Override
public List<LessonProgressDTO> getProgressByStudent(String username) {
    List<LessonProgress> all = progressRepository.findByStudentUsername(username);
    return all.stream().map(p -> LessonProgressDTO.builder()
        .lessonId(p.getLesson().getId())
        .courseId(p.getLesson().getCourse().getId())
        .lessonTitle(p.getLesson().getTitle())
        .studentUsername(p.getStudentUsername())
        .completed(p.isCompleted())
        .build()).collect(Collectors.toList());
}

}
