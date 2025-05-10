package lms.step1.Security;

import lms.step1.Implementation.UserDetailsServiceImpl;
import lms.step1.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    private static final List<String> WHITELIST = List.of(
        "/auth/user/register", "/auth/user/login",
        "/auth/register", "/auth/login",
        "/oauth2", "/oauth2/", "/oauth2/authorization/google",
        "/login/oauth2/code/google", "/welcome",
        "/uploads/profile-pictures/**"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ No Authorization header or invalid format. Skipping filter for path: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        String username = null;

        try {
            username = jwtService.extractUsername(token);
            log.info("✅ Extracted username from token: {}", username);
        } catch (Exception e) {
            log.error("❌ Failed to extract username from token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isValid(token, userDetails)) {
                log.info("✅ Token is valid for user: {}", username);
                log.debug("✅ Authorities: {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("🔐 Authentication set for user: {}", username);
            } else {
                log.warn("❌ Token is invalid or expired for user: {}", username);
            }
        } else {
            log.debug("⚠️ Username is null or already authenticated in SecurityContext");
        }

        filterChain.doFilter(request, response);
    }
}
