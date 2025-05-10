package lms.step1.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lms.step1.Service.LessonProgressService;
import java.util.List;
import java.util.stream.Collectors;
import lms.step1.DTO.LessonProgressDTO;
import lms.step1.Response.MessageResponse;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    @PostMapping("/complete")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<EntityModel<MessageResponse>> markLessonComplete(@RequestBody LessonProgressDTO dto) {
        lessonProgressService.markLessonComplete(dto);

        MessageResponse message = new MessageResponse("✅ Lesson marked as completed");

        EntityModel<MessageResponse> model = EntityModel.of(message);
        model.add(linkTo(methodOn(LessonProgressController.class)
                .getProgressForCourse(dto.getCourseId())).withRel("progress"));

        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @GetMapping("/progress/course/{courseId}")
    public ResponseEntity<List<EntityModel<LessonProgressDTO>>> getProgressForCourse(@PathVariable Long courseId) {
        List<LessonProgressDTO> progressList = lessonProgressService.getProgressByCourse(courseId);
        List<EntityModel<LessonProgressDTO>> models = progressList.stream()
                .map(progress -> {
                    EntityModel<LessonProgressDTO> model = EntityModel.of(progress);
                    model.add(linkTo(methodOn(LessonProgressController.class).getProgressForCourse(courseId))
                            .withSelfRel());
                    return model;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(models);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
@GetMapping("/progress/student")
public ResponseEntity<List<EntityModel<LessonProgressDTO>>> getProgressForStudent() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    List<LessonProgressDTO> progressList = lessonProgressService.getProgressByStudent(username);
    List<EntityModel<LessonProgressDTO>> models = progressList.stream()
        .map(EntityModel::of)
        .collect(Collectors.toList());
    return ResponseEntity.ok(models);
}

}