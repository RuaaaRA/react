package lms.step1.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lms.step1.DTO.LessonDTO;
import lms.step1.DTO.LessonResourceDTO;
import lms.step1.Service.LessonService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
@Slf4j
public class LessonController {

    private final LessonService lessonService;

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @PostMapping("/create")
    public ResponseEntity<EntityModel<LessonDTO>> createLesson(@RequestBody LessonDTO dto) {
        log.info("📘 Creating new lesson for courseId={}", dto.getCourseId());

        if (dto.getCourseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course ID is required.");
        }

        if (!lessonService.isCourseExists(dto.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with ID " + dto.getCourseId() + " not found.");
        }

        if (dto.getVideoUrl() == null) dto.setVideoUrl("");
        if (dto.getPdfUrl() == null) dto.setPdfUrl("");
        if (dto.getAudioUrl() == null) dto.setAudioUrl("");

        LessonDTO created = lessonService.createLesson(dto);
        log.info("✅ Lesson created with ID: {}", created.getId());

        EntityModel<LessonDTO> model = EntityModel.of(created);
        model.add(linkTo(methodOn(LessonController.class).getLessonsByCourseId(dto.getCourseId())).withRel("course-lessons"));
        model.add(linkTo(methodOn(LessonController.class).getLessonById(created.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EntityModel<LessonDTO>>> getLessonsByCourseId(@PathVariable Long courseId) {
        log.info("📥 Fetching lessons for courseId={}", courseId);
        
        // أضف تحقق من وجود الكورس
        if (!lessonService.isCourseExists(courseId)) {
            log.warn("Course not found with ID: {}", courseId);
            return ResponseEntity.notFound().build();
        }
        
        List<LessonDTO> lessons = lessonService.getLessonsByCourseId(courseId);
        List<EntityModel<LessonDTO>> models = lessons.stream()
                .map(lesson -> {
                    EntityModel<LessonDTO> model = EntityModel.of(lesson);
                    model.add(linkTo(methodOn(LessonController.class).getLessonById(lesson.getId())).withSelfRel());
                    model.add(linkTo(methodOn(LessonController.class).getLessonsByCourseId(courseId)).withRel("course-lessons"));
                    return model;
                }).collect(Collectors.toList());

        log.info("✅ Found {} lesson(s) for courseId={}", models.size(), courseId);
        return ResponseEntity.ok(models);
    }
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @GetMapping("/{lessonId}")
    public ResponseEntity<EntityModel<LessonDTO>> getLessonById(@PathVariable Long lessonId) {
        log.info("📥 Fetching lesson with ID: {}", lessonId);

        LessonDTO lesson = lessonService.getLessonById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found with ID: " + lessonId));

        EntityModel<LessonDTO> model = EntityModel.of(lesson);
        model.add(linkTo(methodOn(LessonController.class).getLessonById(lessonId)).withSelfRel());
        model.add(linkTo(methodOn(LessonController.class).getLessonsByCourseId(lesson.getCourseId())).withRel("course-lessons"));

        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @PutMapping("/{lessonId}")
    public ResponseEntity<EntityModel<LessonDTO>> updateLesson(@PathVariable Long lessonId, @RequestBody LessonDTO dto) {
        log.info("✏️ Updating lessonId={} for courseId={}", lessonId, dto.getCourseId());

        if (!lessonService.isLessonExists(lessonId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson with ID " + lessonId + " not found.");
        }

        LessonDTO updatedLesson = lessonService.updateLesson(lessonId, dto);
        log.info("✅ Lesson updated: {}", updatedLesson.getId());

        EntityModel<LessonDTO> model = EntityModel.of(updatedLesson);
        model.add(linkTo(methodOn(LessonController.class).getLessonById(lessonId)).withSelfRel());
        model.add(linkTo(methodOn(LessonController.class).getLessonsByCourseId(dto.getCourseId())).withRel("course-lessons"));

        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        log.warn("❌ Deleting lesson with ID: {}", lessonId);
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @GetMapping("/lessons/{lessonId}/resources")
    public ResponseEntity<List<LessonResourceDTO>> getLessonResources(@PathVariable Long lessonId) {
        List<LessonResourceDTO> resources = lessonService.getResourcesByLessonId(lessonId);
        return ResponseEntity.ok(resources);
    }
    

}