package newJira.system.service;

import newJira.system.dto.auth.AuthResponseDto;
import newJira.system.dto.auth.LoginRequestDto;
import newJira.system.dto.auth.RegisterRequestDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Role;
import newJira.system.exception.custom.BadRequestException;
import newJira.system.exception.custom.UnauthorizedException;
import newJira.system.repository.UserRepository;
import newJira.system.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginRequestDto;
    private RegisterRequestDto registerRequestDto;
    private AppUser user;

    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String JWT = "jwt-token";

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(EMAIL);
        loginRequestDto.setPassword(PASSWORD);

        registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail(EMAIL);
        registerRequestDto.setPassword(PASSWORD);
        registerRequestDto.setRole("USER");

        user = new AppUser();
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("Успешный вход пользователя")
    void loginSuccessTest() {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(JWT);

        AuthResponseDto result = authService.login(loginRequestDto);

        assertEquals(JWT, result.getToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("Ошибка входа — неверные данные")
    void loginFailureTest() {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException());

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequestDto));
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void registerSuccessTest() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(AppUser.class))).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(JWT);

        AuthResponseDto result = authService.register(registerRequestDto);

        assertEquals(JWT, result.getToken());
        verify(userRepository).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Ошибка регистрации — email уже используется")
    void registerEmailExistsTest() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(registerRequestDto));
        verify(userRepository, never()).save(any());
    }
}