package lms.step1.Controller;

import lms.step1.Repository.UserRepository;
import lms.step1.Service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lms.step1.Model.User;
import lms.step1.Request.UpdateUserRequest;
import lms.step1.Response.MessageResponse;
import lms.step1.Response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PutMapping("/update-profile")
    public ResponseEntity<EntityModel<MessageResponse>> updateProfile(
            Authentication authentication,
            @RequestPart("firstName") String firstName,
            @RequestPart("lastName") String lastName,
            @RequestPart(value = "password", required = false) String password,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        String username = authentication.getName();
        log.info("🛠️ Attempting to update profile for user: {}", username);
        log.info("📝 Received profile picture: {}", profilePicture != null ? profilePicture.getOriginalFilename() : "null");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("❌ User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        // Store old profile picture path for deletion
        String oldProfilePicture = user.getProfilePicture();
        log.info("📸 Current profile picture: {}", oldProfilePicture);

        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
            log.info("🔐 Password updated for user: {}", username);
        }

        // Handle profile picture update
        if (profilePicture != null) {
            try {
                log.info("🖼️ Processing profile picture update for user: {}", username);
                log.info("📄 Profile picture details - Name: {}, Size: {}, Type: {}", 
                    profilePicture.getOriginalFilename(),
                    profilePicture.getSize(),
                    profilePicture.getContentType());

                if (profilePicture.isEmpty() || profilePicture.getOriginalFilename().isEmpty()) {
                    log.info("🔄 Request to remove profile picture for user: {}", username);
                    // If empty file is sent, remove the profile picture
                    if (user.getProfilePicture() != null) {
                        try {
                            // Delete the old file
                            Path uploadDir = Paths.get("uploads/profile-pictures");
                            Path oldFilePath = uploadDir.resolve(user.getProfilePicture());
                            log.info("🗑️ Attempting to delete old profile picture: {}", oldFilePath);
                            
                            if (Files.exists(oldFilePath)) {
                                Files.delete(oldFilePath);
                                log.info("✅ Old profile picture deleted successfully");
                            } else {
                                log.warn("⚠️ Old profile picture file not found: {}", oldFilePath);
                            }
                        } catch (IOException e) {
                            log.error("❌ Error deleting old profile picture: {}", e.getMessage());
                            // Continue with the update even if deletion fails
                        }
                    }
                    user.setProfilePicture(null);
                    log.info("✅ Profile picture removed from user record");
                } else {
                    // Validate file type
                    String contentType = profilePicture.getContentType();
                    log.info("📄 File content type: {}", contentType);
                    if (contentType == null || !contentType.startsWith("image/")) {
                        throw new RuntimeException("Invalid file type. Please upload an image.");
                    }

                    // Validate file size (max 5MB)
                    long fileSize = profilePicture.getSize();
                    log.info("📦 File size: {} bytes", fileSize);
                    if (fileSize > 5 * 1024 * 1024) {
                        throw new RuntimeException("File size should be less than 5MB");
                    }

                    // Generate unique filename
                    String originalFilename = profilePicture.getOriginalFilename();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String filename = UUID.randomUUID().toString() + extension;
                    log.info("📁 Generated filename: {}", filename);

                    // Save file
                    Path uploadDir = Paths.get("uploads/profile-pictures");
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                        log.info("📁 Created upload directory: {}", uploadDir);
                    }
                    Path filePath = uploadDir.resolve(filename);
                    Files.copy(profilePicture.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    log.info("💾 File saved to: {}", filePath);

                    // Delete old profile picture if it exists
                    if (user.getProfilePicture() != null) {
                        try {
                            Path oldFilePath = uploadDir.resolve(user.getProfilePicture());
                            if (Files.exists(oldFilePath)) {
                                Files.delete(oldFilePath);
                                log.info("🗑️ Old profile picture deleted: {}", oldFilePath);
                            }
                        } catch (IOException e) {
                            log.error("❌ Error deleting old profile picture: {}", e.getMessage());
                            // Continue with the update even if deletion fails
                        }
                    }

                    // Update user's profile picture
                    user.setProfilePicture(filename);
                    log.info("🖼️ Profile picture updated to: {}", filename);
                }
            } catch (IOException e) {
                log.error("❌ Error handling profile picture: {}", e.getMessage());
                throw new RuntimeException("Error handling profile picture: " + e.getMessage());
            }
        }

        userRepository.save(user);
        log.info("✅ Profile updated successfully for user: {}", username);

        // Generate new token
        String newToken = jwtService.generateAccessToken(user);
        log.info("🔑 New token generated for user: {}", username);

        // Refresh the security context
        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        MessageResponse messageResponse = new MessageResponse("✅ Profile updated successfully");
        EntityModel<MessageResponse> resource = EntityModel.of(messageResponse);
        resource.add(linkTo(methodOn(UserController.class).updateProfile(null, null, null, null, null)).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getMyProfile(null)).withRel("profile"));
        
        // Add profile picture URL to the response
        if (user.getProfilePicture() != null) {
            String profilePictureUrl = "http://localhost:8080/uploads/profile-pictures/" + user.getProfilePicture();
            log.info("🔗 Adding profile picture link to response: {}", profilePictureUrl);
            resource.add(Link.of(profilePictureUrl, "profile-picture"));
        }
        resource.add(Link.of(newToken, "token"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/my-profile")
    public EntityModel<UserProfileResponse> getMyProfile(Authentication authentication) {
        String username = authentication.getName();
        log.info("📄 Fetching profile for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("❌ User not found when accessing profile: {}", username);
                    return new RuntimeException("User not found");
                });

        UserProfileResponse profile = new UserProfileResponse(user);
        EntityModel<UserProfileResponse> model = EntityModel.of(profile);
        model.add(linkTo(methodOn(UserController.class).getMyProfile(authentication)).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).updateProfile(authentication, null, null, null, null)).withRel("update-profile"));
        
        // Add profile picture URL to the response
        if (user.getProfilePicture() != null) {
            String profilePictureUrl = "http://localhost:8080/uploads/profile-pictures/" + user.getProfilePicture();
            log.info("🔗 Adding profile picture link to response: {}", profilePictureUrl);
            model.add(Link.of(profilePictureUrl, "profile-picture"));
        }

        return model;
    }
}
