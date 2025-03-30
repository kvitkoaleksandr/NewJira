package newJira.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtTokenFilter jwtTokenFilter;

    private static final String TOKEN = "valid.jwt.token";
    private static final String EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Успешная установка аутентификации при валидном токене")
    void validTokenAuthenticationSuccessTest() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);
        when(jwtTokenProvider.validateToken(TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUserEmailFromJWT(TOKEN)).thenReturn(EMAIL);

        UserDetails userDetails = User.builder()
                .username(EMAIL)
                .password("encoded")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertEquals(EMAIL, auth.getName());
        assertTrue(auth.isAuthenticated());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Токен отсутствует — фильтр просто продолжает цепочку")
    void noTokenProvidedTest() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Невалидный токен — фильтр не устанавливает аутентификацию")
    void invalidTokenTest() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);
        when(jwtTokenProvider.validateToken(TOKEN)).thenReturn(false);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}