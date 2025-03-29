package newJira.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newJira.system.dto.AuthResponseDto;
import newJira.system.dto.LoginRequestDto;
import newJira.system.dto.RegisterRequestDto;
import newJira.system.entity.AppUser;
import newJira.system.entity.Role;
import newJira.system.exception.custom.BadRequestException;
import newJira.system.exception.custom.UnauthorizedException;
import newJira.system.repository.UserRepository;
import newJira.system.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            return authenticateAndReturnToken(
                    loginRequestDto.getEmail(),
                    loginRequestDto.getPassword()
            );
        } catch (Exception e) {
            log.warn("Неудачная попытка входа для email: {}", loginRequestDto.getEmail());
            throw new UnauthorizedException("Неверный email или пароль");
        }
    }

    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new BadRequestException("Email уже используется");
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

        return authenticateAndReturnToken(registerRequestDto.getEmail(), registerRequestDto.getPassword());
    }

    private AuthResponseDto authenticateAndReturnToken(String email, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );
        log.info("Токен успешно сгенерирован для пользователя: {}", email);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return new AuthResponseDto(jwt);
    }
}