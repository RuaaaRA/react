package lms.step1.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lms.step1.DTO.AssignmentDTO;
import lms.step1.DTO.AssignmentSubmissionDTO;
import lms.step1.Model.Assignment;
import lms.step1.Service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/assignments", produces = "application/json")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> createAssignment(
            @RequestParam("assignmentDTO") String assignmentDTOJson,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("courseId") Long courseId) {

        try {
            AssignmentDTO assignmentDTO = objectMapper.readValue(assignmentDTOJson, AssignmentDTO.class);

            if (file == null || file.isEmpty()) {
                log.warn("⚠️ Missing or empty file in assignment creation.");
                return ResponseEntity.badRequest().body("⚠️ File is missing or empty. Please upload a valid file.");
            }

            log.info("📄 Creating assignment for course ID {} with title: {}", courseId, assignmentDTO.getTitle());

            AssignmentDTO savedAssignment = assignmentService.createAssignment(assignmentDTO, courseId, file);

            log.info("✅ Assignment created with ID: {}", savedAssignment.getId());

            EntityModel<AssignmentDTO> resource = EntityModel.of(savedAssignment);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssignmentController.class)
                    .getAssignmentById(savedAssignment.getId())).withSelfRel();
            resource.add(selfLink);

            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
        } catch (JsonProcessingException e) {
            log.error("❌ Error parsing JSON assignmentDTO: {}", e.getMessage());
            return ResponseEntity.badRequest().body("⚠️ Error parsing JSON: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN','STUDENT')")
    @GetMapping("/{assignmentId}")
    public ResponseEntity<EntityModel<AssignmentDTO>> getAssignmentById(@PathVariable Long assignmentId) {
        log.info("🔍 Fetching assignment with ID: {}", assignmentId);

        AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(assignmentId);

        EntityModel<AssignmentDTO> resource = EntityModel.of(assignmentDTO);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssignmentController.class)
                .getAssignmentById(assignmentId)).withSelfRel();
        resource.add(selfLink);

        Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssignmentController.class)
                .updateAssignment(assignmentId, assignmentDTO)).withRel("update");
        resource.add(updateLink);

        Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssignmentController.class)
                .deleteAssignment(assignmentId)).withRel("delete");
        resource.add(deleteLink);

        return ResponseEntity.ok(resource);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN','STUDENT')")
    @GetMapping
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        log.info("📋 Retrieving all assignments...");
        List<AssignmentDTO> assignments = assignmentService.getAllAssignments();

        for (AssignmentDTO assignment : assignments) {
            assignment.add(Link.of("http://localhost:8080/assignments/" + assignment.getId()).withSelfRel());
            assignment.add(Link.of("http://localhost:8080/assignments/" + assignment.getId() + "/upload").withRel("upload"));
            assignment.add(Link.of("http://localhost:8080/assignments/" + assignment.getId()).withRel("delete"));
        }

        log.info("✅ Found {} assignments", assignments.size());
        return ResponseEntity.ok(assignments);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    @PostMapping(value = "/{assignmentId}/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadAssignmentFile(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            log.warn("⚠️ Empty file upload attempt for assignment ID: {}", assignmentId);
            return ResponseEntity.badRequest().body("⚠️ File is missing or empty. Please upload a valid file.");
        }

        log.info("📤 Uploading file for assignment ID: {}", assignmentId);
        assignmentService.uploadFile(assignmentId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "http://localhost:8080/assignments/" + assignmentId + "/upload")
                .body("✅ File successfully uploaded for Assignment ID: " + assignmentId);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    @PutMapping("/{assignmentId}")
    public ResponseEntity<?> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody AssignmentDTO assignmentDTO) {

        log.info("✏️ Updating assignment with ID: {}", assignmentId);
        AssignmentDTO updatedAssignment = assignmentService.updateAssignment(assignmentId, assignmentDTO);

        EntityModel<AssignmentDTO> resource = EntityModel.of(updatedAssignment);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AssignmentController.class)
                .getAssignmentById(assignmentId)).withSelfRel();
        resource.add(selfLink);

        return ResponseEntity.ok(resource);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long assignmentId) {
        log.warn("🗑️ Deleting assignment with ID: {}", assignmentId);
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.ok("✅ Assignment successfully deleted with ID: " + assignmentId);
    }



   @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN','STUDENT')")
@GetMapping("/{assignmentId}/download")
public ResponseEntity<Resource> downloadAssignmentFile(@PathVariable Long assignmentId) {
    try {
        log.info("⬇️ Downloading file for assignment ID: {}", assignmentId);
        return assignmentService.downloadAssignmentFile(assignmentId);
    } catch (Exception e) {
        log.error("❌ Error downloading file: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
@PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN','STUDENT')")
@GetMapping("/course/{courseId}")
public ResponseEntity<List<AssignmentDTO>> getAssignmentsByCourseId(@PathVariable Long courseId) {
    log.info("📚 Fetching assignments for course ID: {}", courseId);

    List<AssignmentDTO> assignments = assignmentService.getAssignmentsByCourseId(courseId);

    log.info("✅ Found {} assignments for course ID: {}", assignments.size(), courseId);
    return ResponseEntity.ok(assignments);
}


@PreAuthorize("hasAnyAuthority('STUDENT')")
@PostMapping(value = "/{assignmentId}/submit", consumes = "multipart/form-data")
public ResponseEntity<?> submitAssignmentSolution(
        @PathVariable Long assignmentId,
        @RequestParam("solutionText") String solutionText,
        @RequestParam(value = "solutionFile", required = false) MultipartFile solutionFile) {

    try {
        assignmentService.submitSolution(assignmentId, solutionText, solutionFile);
        return ResponseEntity.ok("✅ Solution submitted successfully");
    } catch (Exception e) {
        log.error("Error submitting solution: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("⚠️ Error: " + e.getMessage());
    }
}


@PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
@GetMapping("/{assignmentId}/submissions")
public ResponseEntity<List<AssignmentSubmissionDTO>> getAllSubmissionsForAssignment(@PathVariable Long assignmentId) {
    log.info("📥 Fetching all submissions for assignment ID: {}", assignmentId);

    List<AssignmentSubmissionDTO> submissions = assignmentService.getAllSubmissionsByAssignmentId(assignmentId);

    log.info("✅ Found {} submissions for assignment ID: {}", submissions.size(), assignmentId);
    return ResponseEntity.ok(submissions);
}





}










