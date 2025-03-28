package newJira.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newJira.system.dto.AuthResponseDto;
import newJira.system.dto.LoginRequestDto;
import newJira.system.dto.RegisterRequestDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Role;
import newJira.system.mapper.ManagementMapper;
import newJira.system.repository.UserRepository;
import newJira.system.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth Controller", description = "Example API operations")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ManagementMapper managementMapper;

    @Operation(summary = "Авторизация пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вход выполнен успешно",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"token\": \"eyJhbGciOiJIUzI1NiIs...\" }"))),
            @ApiResponse(responseCode = "401", description = "Неверный email или пароль")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            AuthResponseDto responseDto = authenticateAndReturnToken(
                    loginRequestDto.getEmail(),
                    loginRequestDto.getPassword()
            );
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Ошибка входа для пользователя: {}", loginRequestDto.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
        }
    }
    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован и вошёл в систему",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"token\": \"eyJhbGciOiJIUzI1NiIs...\" }"))),
            @ApiResponse(responseCode = "400", description = "Email уже используется")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email уже используется");
        }

        AppUser appUser = new AppUser();
        appUser.setEmail(registerRequestDto.getEmail());
        appUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        Role userRole = Optional.ofNullable(registerRequestDto.getRole())
                .map(String::toUpperCase)
                .map(Role::valueOf)
                .orElse(Role.USER);
        appUser.setRole(userRole);

        userRepository.save(appUser);

        AuthResponseDto responseDto = authenticateAndReturnToken(
                registerRequestDto.getEmail(),
                registerRequestDto.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    private AuthResponseDto authenticateAndReturnToken(String email, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );
        String jwt = jwtTokenProvider.generateToken(authentication);
        return new AuthResponseDto(jwt);
    }
}