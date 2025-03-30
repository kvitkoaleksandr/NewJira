package newJira.system.security;

import newJira.system.entity.AppUser;
import newJira.system.entity.Role;
import newJira.system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderIntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Генерация, валидация и извлечение email из реального JWT")
    void jwtTokenFlowTest() {
        AppUser userEntity = new AppUser();
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encoded");
        userEntity.setRole(Role.USER);
        userRepository.save(userEntity);

        UserDetails userDetails = User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities("ROLE_USER")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userEntity.getEmail(), jwtTokenProvider.getUserEmailFromJWT(token));
        assertEquals("USER", jwtTokenProvider.getRoleFromToken(token));
        assertFalse(jwtTokenProvider.isAdmin(token));
    }

    @Test
    @DisplayName("Роль ADMIN извлекается корректно")
    void isAdminTokenTrueTest() {
        AppUser admin = new AppUser();
        admin.setEmail("admin@example.com");
        admin.setPassword("encoded");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        UserDetails userDetails = User.builder()
                .username(admin.getEmail())
                .password(admin.getPassword())
                .authorities("ROLE_ADMIN")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertTrue(jwtTokenProvider.isAdmin(token));
    }
}