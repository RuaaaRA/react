package lms.step1.Security;

import lms.step1.User.CustomOAuth2User;
import lms.step1.Enumeration.Provider;
import lms.step1.Model.User;
import lms.step1.Service.JwtService;
import lms.step1.Repository.UserRepository;
import lms.step1.Enumeration.Role;
import lms.step1.Model.Token;
import lms.step1.Repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication) throws IOException {


        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getEmail();

        log.info("🔐 Google OAuth2 login successful for email: {}", email);

        User user = userRepository.findByUsername(email).orElse(null);

        if (user == null) {
            log.info("🆕 New user detected. Creating user for email: {}", email);
            user = User.builder()
                    .username(email)
                    .firstName(oauthUser.getName())
                    .lastName("")
                    .password("oauth2")
                    .role(Role.STUDENT)
                    .provider(Provider.GOOGLE)
                    .build();

            user = userRepository.saveAndFlush(user);
            log.info("✅ New user saved to database with ID: {}", user.getId());
        } else {
            log.info("👤 Existing user found in database: {}", user.getUsername());
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        log.info("🔑 Access and Refresh tokens generated");

        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .loggedOut(false)
                .user(user)
                .build();

        tokenRepository.save(token);
        log.info("💾 Tokens saved to database for user: {}", user.getUsername());

        // تحديث هنا: التوجيه إلى صفحة Dashboard في React مع Token
        String frontendDashboardUrl = "http://localhost:3000/dashboard?token=" + accessToken;
    response.sendRedirect(frontendDashboardUrl);
}
    
    }
