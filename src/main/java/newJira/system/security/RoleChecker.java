package newJira.system.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleChecker {
    private final JwtTokenProvider jwtTokenProvider;

    public boolean isAdmin(HttpServletRequest request) {
        String token = extractToken(request);
        return jwtTokenProvider.isAdmin(token);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return "";
    }
}