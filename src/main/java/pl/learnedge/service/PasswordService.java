package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.learnedge.model.User;
import pl.learnedge.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        User user = getAuthenticatedUser();

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Obecne hasło jest wymagane");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Nowe hasło jest wymagane");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Nowe hasło musi mieć co najmniej 8 znaków");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Obecne hasło jest nieprawidłowe");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Nowe hasło nie może być takie samo jak obecne");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getUsername());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany");
        }

        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails ud) {
            username = ud.getUsername();
        } else if (principal instanceof String s) {
            username = s;
        } else if (principal instanceof User u) {
            username = u.getUsername();
        } else {
            throw new IllegalStateException("Nie można rozpoznać użytkownika");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono użytkownika: " + username));
    }
}
