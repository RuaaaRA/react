package lms.step1.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

@Service
@Slf4j
public class FileStorageService {
    private final Path uploadDir;

    public FileStorageService() throws IOException {
        this.uploadDir = Paths.get("uploads/resources").toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("📁 Created upload directory at: {}", uploadDir);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID().toString() + fileExtension;
            
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            log.info("📤 File stored at: {}", filePath);
            return filename; // إرجاع اسم الملف فقط
            
        } catch (IOException e) {
            log.error("❌ Failed to store file", e);
            throw new RuntimeException("Error saving file", e);
        }
    }

 

    public String getContentType(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            String contentType = Files.probeContentType(filePath);
            
            // تحديد نوع المحتوى يدوياً إذا لم يتم التعرف عليه تلقائياً
            if (contentType == null) {
                if (filename.toLowerCase().endsWith(".mp4")) {
                    contentType = "video/mp4";
                } else if (filename.toLowerCase().endsWith(".webm")) {
                    contentType = "video/webm";
                } else if (filename.toLowerCase().endsWith(".ogg")) {
                    contentType = "video/ogg";
                }
                // أضف المزيد من الصيغ حسب الحاجة
            }
            
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException ex) {
            log.warn("Could not determine file type for: {}", filename);
            return "application/octet-stream";
        }
    }
    // باقي الدوال...
    public Resource loadFileAsResource(String filename) {
        try {
            // استخراج اسم الملف الأساسي بدون مسار
            String baseFilename = filename.contains("/") 
                ? filename.substring(filename.lastIndexOf("/") + 1)
                : filename;
                
            Path filePath = uploadDir.resolve(baseFilename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                throw new RuntimeException("File not found: " + filename);
            }
            
            return resource;
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + filename, ex);
        }
    }

    


    
    public Path getFilePath(String fileUrl) {
        try {
            // استخراج اسم الملف من الرابط الكامل
            String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path path = uploadDir.resolve(filename);
            log.debug("🔍 Getting file path: {}", path.toAbsolutePath());
            return path;
        } catch (Exception e) {
            log.error("❌ Error extracting filename from URL: {}", fileUrl, e);
            throw new RuntimeException("Invalid file URL", e);
        }
    }

    public void deleteFile(String fileUrl) throws IOException {
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = uploadDir.resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("🗑️ Deleted file: {}", filePath.toAbsolutePath());
            } else {
                log.warn("⚠️ Tried to delete non-existent file: {}", filePath.toAbsolutePath());
                throw new IOException("File not found: " + filename);
            }
        } catch (Exception e) {
            log.error("❌ Error deleting file: {}", fileUrl, e);
            throw new IOException("Error deleting file", e);
        }
    }

    
}