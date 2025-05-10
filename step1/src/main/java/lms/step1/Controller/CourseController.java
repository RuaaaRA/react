package lms.step1.Controller;

import lms.step1.DTO.AssignInstructorDTO;
import lms.step1.DTO.CourseDTO;
import lms.step1.DTO.MessageDTO;
import lms.step1.Model.Course;
import lms.step1.Model.User;
import lms.step1.Repository.UserRepository;
import lms.step1.Service.CourseService;
import lms.step1.Service.EmailService;
import lms.step1.Enumeration.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lms.step1.Repository.CourseRepository; // تأكد من وجود هذا الاستيراد


import org.springframework.data.repository.ListCrudRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lms.step1.Enumeration.Role;  // Correct import


import java.util.List;
import java.util.stream.Collectors;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;


    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<EntityModel<CourseDTO>> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        log.info("📚 Creating new course with title: {}", courseDTO.getTitle());
        CourseDTO created = courseService.createCourse(courseDTO);
        log.info("✅ Course created with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(addLinks(created));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CourseDTO>> updateCourse(@PathVariable Long id,
                                                               @RequestBody CourseDTO courseDTO) {
        log.info("✏️ Updating course with ID: {}", id);
        CourseDTO updated = courseService.updateCourse(id, courseDTO);

        emailService.sendEmail(
                "boot83144@gmail.com",
                "Course Updated",
                "The course with ID " + id + " has been updated successfully."
        );
        log.info("📩 Email sent about course update. ID: {}", id);

        return ResponseEntity.ok(addLinks(updated));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<MessageDTO>> deleteCourse(@PathVariable Long id) {
        log.warn("🗑️ Deleting course with ID: {}", id);
        courseService.deleteCourse(id);
        MessageDTO message = new MessageDTO("✅ Course deleted successfully");
        EntityModel<MessageDTO> model = EntityModel.of(message);
        model.add(linkTo(methodOn(CourseController.class).getAllCourses()).withRel("all-courses"));
        return ResponseEntity.ok(model);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CourseDTO>> getCourseById(@PathVariable Long id) {
        log.info("🔍 Fetching course by ID: {}", id);
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(addLinks(course));
    }

   

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/assign-instructor")
    public ResponseEntity<EntityModel<MessageDTO>> assignInstructor(@Valid @RequestBody AssignInstructorDTO request) {
        log.info("🧑‍🏫 Admin assigning instructor ID {} to course ID {}", request.getInstructorId(), request.getCourseId());
        courseService.assignInstructor(request);
        MessageDTO message = new MessageDTO("✅ Instructor assigned to course.");
        EntityModel<MessageDTO> response = EntityModel.of(message);
        response.add(linkTo(methodOn(CourseController.class).getCourseById(request.getCourseId())).withRel("course"));
        return ResponseEntity.ok(response);
    }

    private EntityModel<CourseDTO> addLinks(CourseDTO courseDTO) {
        EntityModel<CourseDTO> model = EntityModel.of(courseDTO);
        model.add(linkTo(methodOn(CourseController.class).getCourseById(courseDTO.getId())).withSelfRel());
        model.add(linkTo(methodOn(CourseController.class).getAllCourses()).withRel("all-courses"));
        return model;
    }
    
 

 @PreAuthorize("hasAuthority('ADMIN')")
@GetMapping("/users/instructors")
public ResponseEntity<List<User>> getAllInstructors() {
    log.info("📋 Fetching all instructors");
    List<User> instructors = userRepository.findByRole(Role.INSTRUCTOR);  // Now using the correct enum
    return ResponseEntity.ok(instructors);
}
@GetMapping
public ResponseEntity<List<CourseDTO>> getAllCourses() {
    List<Course> courses = courseRepository.findAll();
    List<CourseDTO> courseDTOs = courses.stream()
        .map(course -> {
            CourseDTO dto = new CourseDTO();
            dto.setId(course.getId());
            dto.setTitle(course.getTitle());
            dto.setDescription(course.getDescription());
            
            if (course.getInstructor() != null) {
                dto.setInstructor(course.getInstructor());
            }
            
            return dto;
        })
        .collect(Collectors.toList());
    
    return ResponseEntity.ok(courseDTOs);
}


}
