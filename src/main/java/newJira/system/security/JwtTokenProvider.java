package newJira.system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import newJira.system.entity.AppUser;
import newJira.system.entity.Role;
import newJira.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey jwtKey;
    private final UserRepository userRepository;

    private static final long EXPIRATION = 1000 * 60 * 60 * 10;

    public JwtTokenProvider(@Value("${security.jwt.secret}") String secret,
                            UserRepository userRepository) {
        this.jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        AppUser user = userRepository.findByEmail(email);

        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of("role", user.getRole().name()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(jwtKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserEmailFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public boolean isAdmin(String token) {
        return Role.ADMIN.name().equals(getRoleFromToken(token));
    }
}