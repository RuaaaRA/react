package lms.step1.Service;

import lms.step1.DTO.AssignmentDTO;
import lms.step1.DTO.AssignmentSubmissionDTO;
import lms.step1.Model.Assignment;
import lms.step1.Model.AssignmentSubmission;
import lms.step1.Model.Course;
import lms.step1.Model.User;
import lms.step1.Repository.AssignmentRepository;
import lms.step1.Repository.SubmissionRepository;
import lms.step1.Repository.CourseRepository;
import lms.step1.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Transactional
    public AssignmentDTO createAssignment(AssignmentDTO assignmentDTO, Long courseId, MultipartFile file) {
        log.info("Creating assignment for course ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course not found with id: {}", courseId);
                    return new RuntimeException("Course not found with id: " + courseId);
                });

        Assignment assignment = new Assignment();
        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setDescription(assignmentDTO.getDescription());
        assignment.setDueDate(assignmentDTO.getDueDate());
        assignment.setMaxScore(assignmentDTO.getMaxScore());
        assignment.setCourse(course);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment saved with ID: {}", savedAssignment.getId());

        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file, savedAssignment.getId());
            log.info("File uploaded successfully: {}", filePath);
        }

        return new AssignmentDTO(
                savedAssignment.getId(),
                savedAssignment.getTitle(),
                savedAssignment.getDescription(),
                savedAssignment.getDueDate(),
                savedAssignment.getMaxScore(),
                course.getId()
        );
    }

    public void uploadFile(Long assignmentId, MultipartFile file) {
        log.info("Uploading file for assignment ID: {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found with id: {}", assignmentId);
                    return new RuntimeException("Assignment not found with id: " + assignmentId);
                });

        if (file != null && !file.isEmpty()) {
            String filePath = saveFile(file, assignmentId);
            log.info("File uploaded at path: {}", filePath);
        } else {
            log.warn("Empty file received for assignment ID: {}", assignmentId);
        }
    }

    public List<AssignmentDTO> getAllAssignments() {
        log.info("Fetching all assignments...");
        return assignmentRepository.findAll().stream()
                .map(a -> new AssignmentDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getDescription(),
                        a.getDueDate(),
                        a.getMaxScore(),
                        a.getCourse().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public AssignmentDTO updateAssignment(Long assignmentId, AssignmentDTO assignmentDTO) {
        log.info("Updating assignment with ID: {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found with id: {}", assignmentId);
                    return new RuntimeException("Assignment not found with id: " + assignmentId);
                });

        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setDescription(assignmentDTO.getDescription());
        assignment.setDueDate(assignmentDTO.getDueDate());
        assignment.setMaxScore(assignmentDTO.getMaxScore());

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment updated with ID: {}", updatedAssignment.getId());

        return new AssignmentDTO(
                updatedAssignment.getId(),
                updatedAssignment.getTitle(),
                updatedAssignment.getDescription(),
                updatedAssignment.getDueDate(),
                updatedAssignment.getMaxScore(),
                updatedAssignment.getCourse().getId()
        );
    }

    @Transactional
    public void deleteAssignment(Long assignmentId) {
        log.info("Deleting assignment with ID: {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found with id: {}", assignmentId);
                    return new RuntimeException("Assignment not found with id: " + assignmentId);
                });

        assignmentRepository.delete(assignment);
        log.info("Assignment deleted with ID: {}", assignmentId);
    }

    public AssignmentDTO getAssignmentById(Long assignmentId) {
        log.info("Fetching assignment with ID: {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found with id: {}", assignmentId);
                    return new RuntimeException("Assignment not found with id: " + assignmentId);
                });

        return new AssignmentDTO(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getMaxScore(),
                assignment.getCourse().getId()
        );
    }

    public List<AssignmentDTO> getAssignmentsByCourseId(Long courseId) {
        log.info("🔍 Fetching assignments for course ID: {}", courseId);
    
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("❌ Course not found with id: {}", courseId);
                    return new RuntimeException("Course not found with id: " + courseId);
                });
    
        return assignmentRepository.findByCourse(course).stream()
                .map(a -> new AssignmentDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getDescription(),
                        a.getDueDate(),
                        a.getMaxScore(),
                        course.getId()
                )).collect(Collectors.toList());
    }
    
    private String saveFile(MultipartFile file, Long assignmentId) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
    
            String fileName = "assignment_" + assignmentId + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());
    
            // حفظ المسار داخل الـ assignment
            Assignment assignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));
    
            assignment.setFilePath(filePath.toString());
            assignmentRepository.save(assignment);
    
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }
    }
    
    public ResponseEntity<Resource> downloadAssignmentFile(Long assignmentId) throws IOException {
        log.info("Downloading file for assignment ID: {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found with id: {}", assignmentId);
                    return new RuntimeException("Assignment not found with id: " + assignmentId);
                });

        if (assignment.getFilePath() == null || assignment.getFilePath().isEmpty()) {
            log.warn("No file found for assignment ID: {}", assignmentId);
            throw new RuntimeException("No file found for this assignment");
        }

        Path path = Paths.get(assignment.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            log.error("File not found or not readable: {}", path);
            throw new RuntimeException("Could not read the file");
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    public void submitSolution(Long assignmentId, String solutionText, MultipartFile solutionFile) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        User currentStudent = getCurrentStudent();
        
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(currentStudent);
        submission.setSolutionText(solutionText);
        submission.setSubmissionDate(LocalDateTime.now());
        
        if (solutionFile != null && !solutionFile.isEmpty()) {
            String filePath = storeFile(solutionFile);
            submission.setSolutionFilePath(filePath);
        }
        
        submissionRepository.save(submission);
    }
    
    private User getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path targetLocation = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName, ex);
        }
    }
public List<AssignmentSubmissionDTO> getAllSubmissionsByAssignmentId(Long assignmentId) {
    List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentId(assignmentId);

    return submissions.stream()
            .map(submission -> AssignmentSubmissionDTO.builder()
                    .id(submission.getId())
                    .studentId(submission.getStudent().getId()) // ✅ نجلب ID الطالب من كلاس User
                    .solutionText(submission.getSolutionText())
                    .solutionFileUrl(submission.getSolutionFilePath())
                    .submissionDate(submission.getSubmissionDate())
                    .build())
            .collect(Collectors.toList());
}

}