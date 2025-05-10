package lms.step1.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lms.step1.Service.AuthenticationService;
import lms.step1.Response.AuthenticationResponse;
import lms.step1.Request.RegisterRequest;
import lms.step1.Model.User;
import lms.step1.Request.LoginRequest;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        log.info("📝 Registering new user: {}", request.getUsername());

        authService.register(request);

        log.info("✅ User '{}' registered successfully", request.getUsername());

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        log.info("🔐 Attempting login for username: {}", request.getUsername());

        AuthenticationResponse response = authService.authenticate(request);

        log.info("✅ Login successful for username: {}", request.getUsername());

        response.add(linkTo(methodOn(AuthenticationController.class).login(null)).withSelfRel());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        log.info("🔄 Refresh token request received");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("⚠️ Missing or invalid refresh token header");
            return ResponseEntity.badRequest().body(new AuthenticationResponse(null, null, "Missing refresh token"));
        }

        String refreshToken = authHeader.substring(7);
        AuthenticationResponse response = authService.refreshToken(refreshToken);

        log.info("✅ Access token refreshed successfully");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/verify")
public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(403).body("Invalid or missing token");
    }

    String token = authHeader.substring(7);

    if (authService.validateToken(token)) {
        User user = authService.extractUserFromToken(token);
        return ResponseEntity.ok(Map.of(
            "message", "Token is valid",
            "user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
            )
        ));
    } else {
        return ResponseEntity.status(403).body("Invalid token");
    }
}
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
    if (user == null) {
        return ResponseEntity.status(401).body("Not authenticated");
    }
    
    log.info("📡 Fetching current user data for: {}", user.getUsername());
    
    return ResponseEntity.ok(Map.of(
        "id", user.getId(),
        "username", user.getUsername(),
        "firstName", user.getFirstName(),
        "lastName", user.getLastName(),
        "role", user.getRole(),
        "email", user.getUsername() // Assuming username is email
    ));
}

}


