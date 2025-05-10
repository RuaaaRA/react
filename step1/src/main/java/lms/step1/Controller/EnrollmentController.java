package lms.step1.Controller;

import lms.step1.Service.CourseService;
import lms.step1.Service.EnrollmentService;
import lms.step1.DTO.*;
import lms.step1.Response.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/available-courses")
    public ResponseEntity<List<EntityModel<CourseDTO>>> getAvailableCourses() {
        log.info("[GET] /enrollments/available-courses - Fetching available courses");
        List<CourseDTO> courses = courseService.getAllCourses();
        List<EntityModel<CourseDTO>> models = courses.stream()
                .map(course -> {
                    EntityModel<CourseDTO> model = EntityModel.of(course);
                    model.add(linkTo(methodOn(EnrollmentController.class).getAvailableCourses()).withSelfRel());
                    return model;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(models);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/enroll")
    public ResponseEntity<EntityModel<MessageResponse>> enroll(@RequestBody EnrollmentDTO dto, Authentication auth) {
        String username = auth.getName();
        log.info("[POST] /enrollments/enroll - {} enrolling in course {}", username, dto.getCourseId());
        enrollmentService.enroll(username, dto.getCourseId());
        MessageResponse response = new MessageResponse("✅ Enrolled successfully!");
        EntityModel<MessageResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(EnrollmentController.class).getMyEnrollments(auth)).withRel("my-courses"));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @DeleteMapping("/unenroll")
    public ResponseEntity<EntityModel<MessageResponse>> unenroll(@RequestBody EnrollmentDTO dto, Authentication auth) {
        String username = auth.getName();
        log.warn("[DELETE] /unenroll - {} unenrolling from course {}", username, dto.getCourseId());
        enrollmentService.unenroll(username, dto.getCourseId());
        MessageResponse response = new MessageResponse("❌ Unenrolled successfully.");
        EntityModel<MessageResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(EnrollmentController.class).getMyEnrollments(auth)).withRel("my-courses"));
        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PatchMapping("/mark-complete")
    public ResponseEntity<EntityModel<MessageResponse>> markComplete(@RequestBody EnrollmentDTO dto, Authentication auth) {
        String username = auth.getName();
        log.info("[PATCH] /mark-complete - {} completed course {}", username, dto.getCourseId());
        enrollmentService.markComplete(username, dto.getCourseId());
        MessageResponse response = new MessageResponse("🎉 Course marked as completed.");
        EntityModel<MessageResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(EnrollmentController.class).getMyEnrollments(auth)).withRel("my-courses"));
        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/my-courses")
    public ResponseEntity<List<EntityModel<EnrollmentStatusDTO>>> getMyEnrollments(Authentication auth) {
        String username = auth.getName();
        log.info("[GET] /my-courses - Fetching enrollments for {}", username);
        List<EnrollmentStatusDTO> enrollments = enrollmentService.getMyEnrollments(username);
        List<EntityModel<EnrollmentStatusDTO>> models = enrollments.stream()
                .map(dto -> {
                    EntityModel<EnrollmentStatusDTO> model = EntityModel.of(dto);
                    model.add(linkTo(methodOn(EnrollmentController.class).getMyEnrollments(auth)).withSelfRel());
                    return model;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(models);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/my-courses/{courseId}")
    public ResponseEntity<EntityModel<EnrollmentStatusDTO>> getMyEnrollmentForCourse(@PathVariable Long courseId,
                                                                                    Authentication auth) {
        String username = auth.getName();
        log.info("[GET] /my-courses/{} - Fetching specific enrollment for {}", courseId, username);
        EnrollmentStatusDTO enrollment = enrollmentService.getMyEnrollmentForCourse(username, courseId);
        EntityModel<EnrollmentStatusDTO> model = EntityModel.of(enrollment);
        model.add(linkTo(methodOn(EnrollmentController.class).getMyEnrollments(auth)).withRel("my-courses"));
        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EntityModel<EnrollmentStatusDTO>>> getCourseEnrollments(@PathVariable Long courseId,
                                                                                      Authentication auth) {
        String username = auth.getName();
        log.info("[GET] /course/{} - {} fetching enrollments for their course", courseId, username);
        List<EnrollmentStatusDTO> enrollments = enrollmentService.getCourseEnrollments(courseId, username);
        List<EntityModel<EnrollmentStatusDTO>> models = enrollments.stream()
                .map(dto -> {
                    EntityModel<EnrollmentStatusDTO> model = EntityModel.of(dto);
                    model.add(linkTo(methodOn(EnrollmentController.class).getCourseEnrollments(courseId, auth)).withSelfRel());
                    return model;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(models);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    @PostMapping("/enroll-student")
    public ResponseEntity<EntityModel<MessageResponse>> enrollStudentByAdminOrInstructor(
            @RequestBody EnrollStudentDTO dto, Authentication auth) {
        String username = auth.getName();
        log.info("[POST] /enroll-student - {} enrolling student {} in course {}", username, dto.getStudentUsername(), dto.getCourseId());
        enrollmentService.enrollStudentByAdminOrInstructor(dto, username);
        MessageResponse response = new MessageResponse("📚 Student enrolled by admin/instructor");
        EntityModel<MessageResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(EnrollmentController.class).getCourseEnrollments(dto.getCourseId(), auth))
                .withRel("course-enrollments"));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/remove-student")
    public ResponseEntity<EntityModel<MessageResponse>> removeStudent(@RequestBody RemoveStudentDTO dto,
                                                                     Authentication auth) {
        String username = auth.getName();
        log.warn("[DELETE] /remove-student - {} removing student {} from course {}", username, dto.getStudentUsername(), dto.getCourseId());
        enrollmentService.removeStudentFromCourse(username, dto);
        MessageResponse response = new MessageResponse("❌ Student removed from course.");
        EntityModel<MessageResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(EnrollmentController.class).getCourseEnrollments(dto.getCourseId(), auth))
                .withRel("course-enrollments"));
        return ResponseEntity.ok(model);
    }
}