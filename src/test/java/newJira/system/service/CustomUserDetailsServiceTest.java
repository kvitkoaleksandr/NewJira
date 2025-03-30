package newJira.system.service;

import newJira.system.entity.AppUser;
import newJira.system.exception.custom.NotFoundException;
import newJira.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "hashedPassword";

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
    }

    @Test
    @DisplayName("Успешная загрузка пользователя по email")
    void loadUserByUsernameSuccessTest() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(user);

        UserDetails result = customUserDetailsService.loadUserByUsername(EMAIL);

        assertEquals(EMAIL, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("Ошибка — пользователь не найден")
    void loadUserByUsernameNotFoundTest() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> customUserDetailsService.loadUserByUsername(EMAIL));
    }
}