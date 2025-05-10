package lms.step1.Service;

import lms.step1.Model.User;
import lms.step1.Repository.UserRepository;
import lms.step1.Request.RegisterRequest;
import lms.step1.Request.LoginRequest;
import lms.step1.Response.AuthenticationResponse;
import lms.step1.Model.Token;
import lms.step1.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public void register(RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found: {}", request.getUsername());
                    return new RuntimeException("User not found");
                });

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .loggedOut(false)
                .user(user)
                .build();

        tokenRepository.save(token);

        log.info("Authentication successful for user: {}", request.getUsername());
        return new AuthenticationResponse(accessToken, refreshToken, "Login successful");
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        log.info("Refreshing token...");

        Token storedToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found");
                    return new RuntimeException("Invalid refresh token");
                });

        if (storedToken.isLoggedOut()) {
            log.warn("Refresh token is logged out");
            throw new RuntimeException("Token is logged out");
        }

        User user = storedToken.getUser();

        if (!jwtService.isValid(refreshToken, user)) {
            log.warn("Refresh token is expired or invalid for user: {}", user.getUsername());
            throw new RuntimeException("Token is expired or invalid");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        storedToken.setAccessToken(newAccessToken);
        tokenRepository.save(storedToken);

        log.info("Token refreshed successfully for user: {}", user.getUsername());
        return new AuthenticationResponse(newAccessToken, storedToken.getRefreshToken(), "Token refreshed successfully");
    }

    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return jwtService.isValid(token, user);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }

       public User extractUserFromToken(String token) {
        String username = jwtService.extractUsername(token);  // نستخدم طريقة extractUsername في JwtService لاستخراج اسم المستخدم
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("المستخدم غير موجود: " + username));
    }
    
}
