package lms.step1.Controller;

import lms.step1.DTO.LessonResourceDTO;
import lms.step1.Enumeration.ResourceType;
import lms.step1.Implementation.LessonServicelmpl;
import lms.step1.Model.Lesson;
import lms.step1.Model.LessonResource;
import lms.step1.Repository.LessonRepository;
import lms.step1.Repository.LessonResourceRepository;
import lms.step1.Service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Slf4j
public class LessonResourceController {

    private final LessonResourceRepository resourceRepository;
    private final LessonRepository lessonRepository;
    private final FileStorageService fileStorageService;

    private final LessonServicelmpl resourceService;






    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @PostMapping("/upload")
    public ResponseEntity<EntityModel<LessonResourceDTO>> uploadResource(
            @RequestParam("lessonId") Long lessonId,
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file) {
    
        log.info("📥 Uploading resource for lessonId={} with title='{}' and type={}", lessonId, title, type);
    
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> {
                    log.error("❌ Lesson not found with ID: {}", lessonId);
                    return new RuntimeException("Lesson not found with ID: " + lessonId);
                });
    
        ResourceType resourceType;
        try {
            resourceType = ResourceType.valueOf(type);
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid resource type: {}", type, e);
            return ResponseEntity.badRequest().body(
                    EntityModel.of(LessonResourceDTO.builder().title("Invalid resource type").build())
            );
        }
    
        if (file.isEmpty()) {
            log.error("❌ Uploaded file is empty.");
            return ResponseEntity.badRequest().body(
                    EntityModel.of(LessonResourceDTO.builder().title("Empty file uploaded").build())
            );
        }
    
        // التعديل الرئيسي هنا - استقبال المسار الكامل من fileStorageService
        String fullUrl = fileStorageService.storeFile(file);
        LessonResource resource = new LessonResource(title, resourceType, fullUrl, lesson);
    
        resource = resourceRepository.save(resource);
    
        log.info("✅ Resource uploaded successfully for lessonId={}, resourceId={}", lessonId, resource.getId());
    
        EntityModel<LessonResourceDTO> model = EntityModel.of(mapToDTO(resource));
        model.add(linkTo(methodOn(LessonResourceController.class).getResourcesByLessonId(lessonId)).withRel("lesson-resources"));
    
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN', 'STUDENT')")
    @GetMapping("/download/{resourceId}")
    public ResponseEntity<Resource> downloadResource(@PathVariable Long resourceId) {
        LessonResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with ID: " + resourceId));
    
        // تحميل الملف من الخدمة
        Resource fileResource = fileStorageService.loadFileAsResource(resource.getUrl());
        
        // تحديد امتداد الملف من اسم الملف المخزن
        String filename = resource.getUrl();
        String fileExtension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        
        // تحديد نوع المحتوى بناء على الامتداد
        String contentType;
        switch (fileExtension) {
            case ".mp4":
                contentType = "video/mp4";
                break;
            case ".pdf":
                contentType = "application/pdf";
                break;
            case ".mp3":
                contentType = "audio/mpeg";
                break;
            case ".jpg":
            case ".jpeg":
                contentType = "image/jpeg";
                break;
            case ".png":
                contentType = "image/png";
                break;
            default:
                contentType = "application/octet-stream";
        }
        
        // إعداد headers للاستجابة
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        
        // إذا كان فيديو، عرضه مباشرة. وإلا حمله كملف
        if (contentType.startsWith("video/")) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getTitle() + fileExtension + "\"");
        } else {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getTitle() + fileExtension + "\"");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileResource);
    }
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @PostMapping("/quiz")
    public ResponseEntity<EntityModel<LessonResourceDTO>> addQuizResource(@RequestBody LessonResourceDTO dto) {
        log.info("📝 Adding quiz resource for lessonId={} with title='{}'", dto.getLessonId(), dto.getTitle());

        Lesson lesson = lessonRepository.findById(dto.getLessonId())
                .orElseThrow(() -> {
                    log.error("❌ Lesson not found for quiz upload");
                    return new RuntimeException("Lesson not found");
                });

        if (!dto.getType().equalsIgnoreCase("QUIZ")) {
            log.error("❌ Only QUIZ type resources are allowed here.");
            throw new IllegalArgumentException("Only QUIZ type resources are allowed here.");
        }

        LessonResource resource = new LessonResource(dto.getTitle(), ResourceType.QUIZ, dto.getUrl(), lesson);

        resource = resourceRepository.save(resource);

        log.info("✅ Quiz resource added with ID: {}", resource.getId());

        EntityModel<LessonResourceDTO> model = EntityModel.of(mapToDTO(resource));
        model.add(linkTo(methodOn(LessonResourceController.class).getResourcesByLessonId(dto.getLessonId())).withRel("lesson-resources"));

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        log.info("🗑️ Deleting resource with ID: {}", resourceId);

        LessonResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("❌ Resource not found with ID: {}", resourceId);
                    return new RuntimeException("Resource not found with ID: " + resourceId);
                });

        try {
            fileStorageService.deleteFile(resource.getUrl());
        } catch (IOException e) {
            log.error("❌ Error deleting file: {}", resource.getUrl(), e);
        }

        resourceRepository.delete(resource);
        log.info("✅ Resource deleted with ID: {}", resourceId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")
    @PutMapping("/{resourceId}")
    public ResponseEntity<EntityModel<LessonResourceDTO>> updateResource(
            @PathVariable Long resourceId,
            @RequestBody LessonResourceDTO dto) {

        log.info("✏️ Updating resource with ID: {}", resourceId);

        LessonResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.error("❌ Resource not found with ID: {}", resourceId);
                    return new RuntimeException("Resource not found with ID: " + resourceId);
                });

        if (dto.getTitle() != null) resource.setTitle(dto.getTitle());
        if (dto.getType() != null) {
            try {
                resource.setType(ResourceType.valueOf(dto.getType()));
            } catch (IllegalArgumentException e) {
                log.error("❌ Invalid resource type: {}", dto.getType());
                return ResponseEntity.badRequest().body(null);
            }
        }
        if (dto.getUrl() != null) resource.setUrl(dto.getUrl());

        resource = resourceRepository.save(resource);

        log.info("✅ Resource updated with ID: {}", resource.getId());

        EntityModel<LessonResourceDTO> model = EntityModel.of(mapToDTO(resource));
        model.add(linkTo(methodOn(LessonResourceController.class).getResourcesByLessonId(resource.getLesson().getId())).withRel("lesson-resources"));

        return ResponseEntity.ok(model);
    }

  
      

    private LessonResourceDTO mapToDTO(LessonResource resource) {
        return LessonResourceDTO.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .type(resource.getType().name())
                .url(resource.getUrl())
                .lessonId(resource.getLesson().getId())
                .build();
    }
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ADMIN' , 'STUDENT')")

    @GetMapping("/lesson/{lessonId}")
public ResponseEntity<List<LessonResourceDTO>> getResourcesByLessonId(@PathVariable Long lessonId) {
    System.out.println("Fetching resources for lesson: " + lessonId); // سجل في الخادم
    
    List<LessonResource> resources = resourceRepository.findByLessonId(lessonId);
    System.out.println("Found resources: " + resources.size()); // عدد الموارد الموجودة
    
    List<LessonResourceDTO> dtos = resources.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    
    System.out.println("Returning DTOs: " + dtos); // محتوى الـ DTOs
    
    return ResponseEntity.ok(dtos);
}
  
  

}

