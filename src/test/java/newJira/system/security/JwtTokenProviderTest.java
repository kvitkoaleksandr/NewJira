package newJira.system.security;

import newJira.system.entity.AppUser;
import newJira.system.entity.Role;
import newJira.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        String secret = "o7HVR1j4MoN3TuWYq6fLSh2cEoDvqX9ijj+f4jsjwgI="; // взять из application.yml
        jwtTokenProvider = new JwtTokenProvider(secret, userRepository);
    }

    @Test
    @DisplayName("Генерация и валидация JWT токена")
    void generateAndValidateTokenTest() {
        String email = "test@example.com";
        String password = "encodedPass";

        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword(password);
        appUser.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(appUser);

        User springUser = new User(email, password, Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(springUser, null);

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(email, jwtTokenProvider.getUserEmailFromJWT(token));
        assertEquals("USER", jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Проверка isAdmin по токену")
    void isAdminTest() {
        String email = "admin@example.com";
        String password = "encoded";
        Role role = Role.ADMIN;

        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword(password);
        appUser.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(appUser);

        User user = new User(email, password, Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        String token = jwtTokenProvider.generateToken(authentication);

        boolean isAdmin = jwtTokenProvider.isAdmin(token);

        assertTrue(isAdmin);
    }

    @Test
    @DisplayName("Валидация невалидного токена")
    void invalidTokenTest() {
        String invalidToken = "this.is.not.a.valid.token";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
}