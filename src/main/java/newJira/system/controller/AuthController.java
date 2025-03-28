package newJira.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newJira.system.dto.LoginRequestDto;
import newJira.system.dto.RegisterRequestDto;
import newJira.system.dto.UserDto;
import newJira.system.entity.Role;
import newJira.system.mapper.ManagementMapper;
import newJira.system.entity.AppUser;
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

    @Operation(summary = "User authentication", description = "Login and get JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );
            String jwt = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequestDto.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @Operation(summary = "User registration", description = "Register and get JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email is already in use")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            log.warn("Attempt to register with existing email: {}", registerRequestDto.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }

        AppUser appUser = new AppUser();
        appUser.setEmail(registerRequestDto.getEmail());
        appUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        Role userRole = Optional.ofNullable(registerRequestDto.getRole())
                .map(String::toUpperCase)
                .map(Role::valueOf)
                .orElse(Role.USER);
        appUser.setRole(userRole);

        UserDto userDto = managementMapper.toUserDto(userRepository.save(appUser));
        log.info("User registered successfully: {}", appUser.getEmail());
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
}