package lms.step1.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:${user.home}}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Create the upload directory if it doesn't exist
        File uploadDirFile = new File("uploads/profile-pictures");
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String absolutePath = uploadDirFile.getAbsolutePath();
        System.out.println("Profile pictures directory: " + absolutePath);

        registry.addResourceHandler("/uploads/profile-pictures/**")
                .addResourceLocations("file:" + absolutePath + "/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
} 