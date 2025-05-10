package lms.step1.Implementation;

import lms.step1.Service.CourseService;
import lms.step1.Service.EmailService;
import lms.step1.DTO.AssignInstructorDTO;
import lms.step1.DTO.CourseDTO;
import lms.step1.Exception.CourseAlreadyExistsException;
import lms.step1.Exception.CourseNotFoundException;
import lms.step1.Enumeration.Role;
import lms.step1.Model.User;
import lms.step1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lms.step1.Repository.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;
import lms.step1.Model.Course;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final EmailService emailService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        log.info("📘 Creating new course with title: {}", courseDTO.getTitle());

        if (courseRepository.existsByTitle(courseDTO.getTitle())) {
            log.warn("⚠️ Course already exists: {}", courseDTO.getTitle());
            throw new CourseAlreadyExistsException(courseDTO.getTitle());
        }

        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .duration(courseDTO.getDuration())
                .build();

        Course saved = courseRepository.save(course);
        log.info("✅ Course created with ID: {}", saved.getId());

        return mapToDTO(saved);
    }

    @Override
    public CourseDTO updateCourse(Long courseId, CourseDTO courseDTO) {
        log.info("✏️ Updating course with ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("❌ Course not found with ID: {}", courseId);
                    return new CourseNotFoundException("Course not found with ID: " + courseId);
                });

        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setDuration(courseDTO.getDuration());

        Course updated = courseRepository.save(course);
        log.info("✅ Course updated successfully with ID: {}", updated.getId());

        sendCourseUpdateNotification("boot83144@gmail.com", updated.getTitle(), updated.getDescription(), updated.getDuration());

        return mapToDTO(updated);
    }

    @Override
    public void sendCourseUpdateNotification(String email, String courseTitle, String description, int duration) {
        log.info("📩 Sending course update email to {}", email);
        String subject = "📢 Course Updated: " + courseTitle;
        String message = "The course '" + courseTitle + "' has been updated.\n\n"
                + "Description: " + description + "\n"
                + "Duration: " + duration + " hours\n"
                + "Visit the LMS to check updates.";

        emailService.sendEmail(email, subject, message);
        log.info("✅ Email sent to {}", email);
    }

    @Override
    public void deleteCourse(Long courseId) {
        log.warn("🗑️ Deleting course with ID: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("❌ Course not found with ID: {}", courseId);
                    return new CourseNotFoundException("Course not found with ID: " + courseId);
                });

        courseRepository.delete(course);
        log.info("✅ Course deleted with ID: {}", courseId);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        log.info("📋 Retrieving all courses...");
        return courseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO getCourseById(Long courseId) {
        log.info("🔎 Retrieving course by ID: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("❌ Course not found with ID: {}", courseId);
                    return new CourseNotFoundException("Course not found with ID: " + courseId);
                });
        return mapToDTO(course);
    }

    @Override
    public void assignInstructor(AssignInstructorDTO request) {
        log.info("👨‍🏫 Assigning instructor ID {} to course ID {}", request.getInstructorId(), request.getCourseId());

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> {
                    log.error("❌ Course not found with ID: {}", request.getCourseId());
                    return new CourseNotFoundException("Course not found with ID: " + request.getCourseId());
                });

        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> {
                    log.error("❌ Instructor not found with ID: {}", request.getInstructorId());
                    return new RuntimeException("Instructor not found with ID: " + request.getInstructorId());
                });

        if (instructor.getRole() != Role.INSTRUCTOR) {
            log.error("❌ User with ID {} is not an INSTRUCTOR", request.getInstructorId());
            throw new RuntimeException("User is not an instructor");
        }

        course.setInstructor(instructor);
        courseRepository.save(course);
        log.info("✅ Instructor {} assigned to course {}", instructor.getUsername(), course.getTitle());
    }

    

    private CourseDTO mapToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .duration(course.getDuration())
                .build();
    }
    

    
}
