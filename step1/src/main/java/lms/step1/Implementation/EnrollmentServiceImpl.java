package lms.step1.Implementation;

import lms.step1.Model.Course;
import lms.step1.Repository.CourseRepository;
import lms.step1.Repository.EnrollmentRepository;
import lms.step1.DTO.EnrollStudentDTO;
import lms.step1.DTO.EnrollmentStatusDTO;
import lms.step1.DTO.RemoveStudentDTO;
import lms.step1.Exception.CourseNotFoundException;
import lms.step1.Enumeration.Role;
import lms.step1.Model.User;
import lms.step1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lms.step1.Service.EnrollmentService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lms.step1.Model.Enrollment;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public void enroll(String studentUsername, Long courseId) {
        log.info("📥 Student '{}' attempting to enroll in course ID: {}", studentUsername, courseId);
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        enrollmentRepository.findByStudentAndCourse(student, course).ifPresent(e -> {
            log.warn("⚠️ Student '{}' is already enrolled in course '{}'", studentUsername, course.getTitle());
            throw new RuntimeException("Already enrolled in course");
        });

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .completed(false)
                .enrolledAt(LocalDate.now())
                .build();

        enrollmentRepository.save(enrollment);
        log.info("✅ Student '{}' enrolled in course '{}'", studentUsername, course.getTitle());
    }

    @Override
    public void unenroll(String studentUsername, Long courseId) {
        log.info("📤 Student '{}' attempting to unenroll from course ID: {}", studentUsername, courseId);
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
        log.info("❌ Student '{}' unenrolled from course '{}'", studentUsername, course.getTitle());
    }

    @Override
    public List<EnrollmentStatusDTO> getMyEnrollments(String studentUsername) {
        log.info("📚 Retrieving enrollments for student '{}'", studentUsername);
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByStudent(student).stream()
                .map(this::mapToStatus)
                .collect(Collectors.toList());
    }

    @Override
    public void markComplete(String studentUsername, Long courseId) {
        log.info("🎯 Student '{}' marking course ID {} as complete", studentUsername, courseId);
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setCompleted(true);
        enrollmentRepository.save(enrollment);
        log.info("✅ Course '{}' marked as complete for student '{}'", course.getTitle(), studentUsername);
    }

    @Override
    public List<EnrollmentStatusDTO> getCourseEnrollments(Long courseId, String requesterUsername) {
        log.info("🔎 {} is viewing enrollments for course ID: {}", requesterUsername, courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (requester.getRole() == Role.INSTRUCTOR &&
            (course.getInstructor() == null || !course.getInstructor().getId().equals(requester.getId()))) {
            log.warn("❌ Instructor '{}' is not assigned to course '{}'", requesterUsername, course.getTitle());
            throw new RuntimeException("Instructor is not assigned to this course");
        }

        return enrollmentRepository.findByCourse(course).stream()
                .map(this::mapToStatus)
                .collect(Collectors.toList());
    }

    @Override
    public void enrollStudentByAdminOrInstructor(EnrollStudentDTO dto, String requesterUsername) {
        log.info("📥 {} enrolling student '{}' to course ID: {}", requesterUsername, dto.getStudentUsername(), dto.getCourseId());

        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(requester.getRole() == Role.ADMIN || requester.getRole() == Role.INSTRUCTOR)) {
            log.error("🚫 Unauthorized enroll attempt by {}", requesterUsername);
            throw new RuntimeException("Not authorized");
        }

        User student = userRepository.findByUsername(dto.getStudentUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        enrollmentRepository.findByStudentAndCourse(student, course).ifPresent(e -> {
            log.warn("⚠️ Student '{}' is already enrolled in course '{}'", student.getUsername(), course.getTitle());
            throw new RuntimeException("Already enrolled");
        });

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .completed(false)
                .enrolledAt(LocalDate.now())
                .build();

        enrollmentRepository.save(enrollment);
        log.info("✅ Student '{}' enrolled in course '{}' by {}", student.getUsername(), course.getTitle(), requesterUsername);
    }

    @Override
    public EnrollmentStatusDTO getMyEnrollmentForCourse(String username, Long courseId) {
        log.info("📘 Retrieving enrollment for student '{}' in course ID: {}", username, courseId);
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        return mapToStatus(enrollment);
    }

    @Override
    public void removeStudentFromCourse(String instructorUsername, RemoveStudentDTO dto) {
        log.info("❌ Instructor '{}' attempting to remove student '{}' from course ID: {}", instructorUsername, dto.getStudentUsername(), dto.getCourseId());

        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (course.getInstructor() == null || !course.getInstructor().getId().equals(instructor.getId())) {
            log.warn("🚫 Instructor '{}' is not assigned to course ID: {}", instructorUsername, dto.getCourseId());
            throw new RuntimeException("You are not assigned to this course");
        }

        User student = userRepository.findByUsername(dto.getStudentUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Student is not enrolled in this course"));

        enrollmentRepository.delete(enrollment);
        log.info("✅ Student '{}' removed from course '{}' by instructor '{}'", student.getUsername(), course.getTitle(), instructorUsername);
    }

    private EnrollmentStatusDTO mapToStatus(Enrollment enrollment) {
        return EnrollmentStatusDTO.builder()
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .completed(enrollment.isCompleted())
                .enrolledAt(enrollment.getEnrolledAt().toString())
                .studentUsername(enrollment.getStudent().getUsername())
                .build();
    }
}
