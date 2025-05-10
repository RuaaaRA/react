package lms.step1.Service;

import lms.step1.Security.JwtProperties;
import lms.step1.Model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;

    public String extractUsername(String token) {
        log.debug("🔍 Extracting username from token...");
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            log.debug("📦 Extracting all claims from token...");
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("⚠️ Token has expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("❌ Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public String generateAccessToken(User user) {
        log.info("🔐 Generating access token for user: {}", user.getUsername());
        return generateToken(user, jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(User user) {
        log.info("🔐 Generating refresh token for user: {}", user.getUsername());
        return generateToken(user, jwtProperties.getAccessTokenExpiration() * 7);
    }

    public boolean isValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean valid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        log.info("✅ Token validation result for {}: {}", username, valid);
        return valid;
    }

    private String generateToken(User user, long expirationMillis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("📤 Token generated for {} with expiration {} ms", user.getUsername(), expirationMillis);
        return token;
    }

    private Key getSignInKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractClaim(token, Claims::getExpiration).before(new Date());
        if (expired) {
            log.warn("⚠️ Token is expired");
        }
        return expired;
    }
}
