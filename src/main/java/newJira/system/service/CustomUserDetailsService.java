package newJira.system.service;

import lombok.extern.slf4j.Slf4j;
import newJira.system.entity.AppUser;
import newJira.system.exception.custom.NotFoundException;
import newJira.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(email);
        if (appUser == null) {
            log.warn("Не удалось найти пользователя с email {}", email);
            throw new NotFoundException("Пользователь не найден с email: " + email);
        }

        log.debug("Пользователь с email {} успешно загружен", email);

        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}