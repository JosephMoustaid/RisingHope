package spring.charityapp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import spring.charityapp.common.Role;
import spring.charityapp.exceptions.InvalidTokenException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    // Configuration - move these to application.properties in production
    private static final String SECRET_KEY = "#  remove for securituy reasons";

    private static final long ACCESS_TOKEN_EXPIRATION = 86400000; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days

    // expiration and refresh tokens expiration durations
    private static final long ADMIN_ACCESS_TOKEN_EXPIRATION = 3600000; // 1 hours
    private static final long ADMIN_REFRESH_TOKEN_EXPIRATION = 28800000; // 8 hours
    // Generate both access and refresh tokens
    public static Map<String, String> generateTokens(int userId, Role role) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", buildToken(userId, role, ACCESS_TOKEN_EXPIRATION));
        tokens.put("refresh_token", buildToken(userId, role, REFRESH_TOKEN_EXPIRATION));
        return tokens;
    }


    // generate admin tokens (which expire faster)
    public static Map<String, String> generateAdminTokens(int userId) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", buildToken(userId, Role.Admin, ADMIN_ACCESS_TOKEN_EXPIRATION));
        tokens.put("refresh_token", buildToken(userId, Role.Admin, ADMIN_REFRESH_TOKEN_EXPIRATION));
        return tokens;
    }


    private static String buildToken(int userId, Role role, long expiration) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)  // Changed this line
                .compact();
    }



    // Improved token validation
    public static TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())  // Changed from SECRET_KEY to getSecretKey()
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new TokenValidationResult(
                    Integer.parseInt(claims.getSubject()),
                    Role.valueOf(claims.get("role", String.class)),
                    claims.getExpiration()
            );
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token expired");
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid token signature");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token");
        }
    }


    // Add this helper method
    private static SecretKey getSecretKey() {
        // If using external configuration:
        // return Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));

        // If using static key:
        return Keys.hmacShaKeyFor("#  remove for securituy reasons".getBytes(StandardCharsets.UTF_8));
    }

    // Token validation result container
    public record TokenValidationResult(int userId, Role role, Date expiration) {}
}