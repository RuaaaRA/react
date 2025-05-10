package lms.step1.Implementation;

import lms.step1.DTO.LessonDTO;
import lms.step1.DTO.LessonResourceDTO;
import lms.step1.Model.Lesson;
import lms.step1.Model.LessonResource;
import lms.step1.Repository.CourseRepository;
import lms.step1.Repository.LessonRepository;
import lms.step1.Repository.LessonResourceRepository;
import lms.step1.Service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServicelmpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonResourceRepository lessonResourceRepository;

    @Override
    public LessonDTO createLesson(LessonDTO dto) {
        Lesson lesson = Lesson.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .pdfUrl(dto.getPdfUrl())
                .audioUrl(dto.getAudioUrl())
                .course(courseRepository.findById(dto.getCourseId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found")))
                .build();

        Lesson saved = lessonRepository.save(lesson);
        return toDTO(saved);
    }

    @Override
    public boolean isCourseExists(Long courseId) {
        boolean exists = courseRepository.existsById(courseId);
        log.info("🔎 Checking if course with ID {} exists: {}", courseId, exists);
        return exists;
    }

    @Override
    public boolean isLessonExists(Long lessonId) {
        boolean exists = lessonRepository.existsById(lessonId);
        log.info("🔎 Checking if lesson with ID {} exists: {}", lessonId, exists);
        return exists;
    }

    @Override
    public List<LessonDTO> getLessonsByCourseId(Long courseId) {
        log.info("📥 Retrieving lessons for course ID: {}", courseId);
        return lessonRepository.findByCourseId(courseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LessonDTO> getLessonById(Long lessonId) {
        log.info("📥 Retrieving lesson with ID: {}", lessonId);
        return lessonRepository.findById(lessonId)
                .map(this::toDTO);
    }

    @Override
    public LessonDTO updateLesson(Long lessonId, LessonDTO dto) {
        log.info("✏️ Updating lesson with ID: {}", lessonId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());
        lesson.setVideoUrl(dto.getVideoUrl());
        lesson.setPdfUrl(dto.getPdfUrl());
        lesson.setAudioUrl(dto.getAudioUrl());

        Lesson updated = lessonRepository.save(lesson);
        return toDTO(updated);
    }

    @Override
    public void deleteLesson(Long lessonId) {
        log.info("🗑️ Deleting lesson with ID: {}", lessonId);
        if (!lessonRepository.existsById(lessonId)) {
            log.warn("❌ Lesson not found for deletion with ID: {}", lessonId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found");
        }
        lessonRepository.deleteById(lessonId);
        log.info("✅ Lesson deleted successfully with ID: {}", lessonId);
    }

    

    // ===== Helper methods =====

    private LessonDTO toDTO(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .videoUrl(lesson.getVideoUrl())
                .pdfUrl(lesson.getPdfUrl())
                .audioUrl(lesson.getAudioUrl())
                .courseId(lesson.getCourse().getId())
                .build();
    }

   



    public List<LessonResourceDTO> getResourcesByLessonId(Long lessonId) {
        List<LessonResource> resources = lessonResourceRepository.findByLessonId(lessonId);
        return resources.stream().map(this::convertToLessonResourceDTO).collect(Collectors.toList());
    }

    private LessonResourceDTO convertToLessonResourceDTO(LessonResource resource) {
        return new LessonResourceDTO(
            resource.getId(),
            resource.getTitle(),  // تأكد من أن لديك اسم صحيح في LessonResource
            resource.getType().name(),  // فرضًا أن نوع الـ resource هو Enum
            resource.getUrl(),
            resource.getLesson().getId() // إذا كنت تحتاج ID للدورة التي تنتمي إليها الـ Lesson
        );
    }


    
    
}
