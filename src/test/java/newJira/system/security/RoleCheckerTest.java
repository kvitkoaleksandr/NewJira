package newJira.system.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleCheckerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RoleChecker roleChecker;

    private static final String TOKEN = "jwt-token";
    private static final String EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);
    }

    @Test
    @DisplayName("Проверка isAdmin — пользователь админ")
    void isAdminTrueTest() {
        when(jwtTokenProvider.isAdmin(TOKEN)).thenReturn(true);

        boolean result = roleChecker.isAdmin(request);

        assertTrue(result);
        verify(jwtTokenProvider).isAdmin(TOKEN);
    }

    @Test
    @DisplayName("Проверка isAdmin — пользователь не админ")
    void isAdminFalseTest() {
        when(jwtTokenProvider.isAdmin(TOKEN)).thenReturn(false);

        boolean result = roleChecker.isAdmin(request);

        assertFalse(result);
        verify(jwtTokenProvider).isAdmin(TOKEN);
    }

    @Test
    @DisplayName("Получение email из токена")
    void getCurrentEmailSuccessTest() {
        when(jwtTokenProvider.getUserEmailFromJWT(TOKEN)).thenReturn(EMAIL);

        String result = roleChecker.getCurrentEmail(request);

        assertEquals(EMAIL, result);
        verify(jwtTokenProvider).getUserEmailFromJWT(TOKEN);
    }
}