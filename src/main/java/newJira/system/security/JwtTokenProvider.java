package newJira.system.security;

import io.jsonwebtoken.Claims;
import newJira.system.entity.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import newJira.system.entity.Role;
import newJira.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey jwtKey;
    private final UserRepository userRepository;
    private static final int JWT_EXPIRATION_IN_MS = 36000000;
    private static final String AUTHENTICATION_NULL_ERROR = "Authentication cannot be null or unauthenticated";
    private static final String CLAIMS_NULL_ERROR = "Claims cannot be null";
    private static final String TOKEN_PARSE_ERROR = "Failed to parse token";

    public JwtTokenProvider(@Value("${security.jwt.secret}") String jwtKey, UserRepository userRepository) {
        this.jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtKey));
        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error(AUTHENTICATION_NULL_ERROR);
            throw new IllegalArgumentException(AUTHENTICATION_NULL_ERROR);
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        AppUser appUser = userRepository.findByEmail(username);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", appUser.getRole().name());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_IN_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtKey)
                .compact();
    }

    public String getUserEmailFromJWT(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) {
            log.error(CLAIMS_NULL_ERROR);
            throw new IllegalArgumentException(CLAIMS_NULL_ERROR);
        }
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtKey)
                    .build()
                    .parseClaimsJws(token);
            Claims claims = parseClaims(token);
            if (claims.getExpiration().before(new Date())) {
                log.error("Token expired: {}", token);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Token validation failed: {}", token, e);
            return false;
        }
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error(TOKEN_PARSE_ERROR, e);
            throw new IllegalArgumentException(TOKEN_PARSE_ERROR, e);
        }
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    public boolean isAdmin(String token) {
        String role = getRoleFromToken(token);
        return Role.ADMIN.name().equals(role);
    }
}