package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.learnedge.model.PasswordResetToken;
import pl.learnedge.model.User;
import pl.learnedge.repository.PasswordResetTokenRepository;
import pl.learnedge.repository.UserRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepo;

    private static final SecureRandom RANDOM = new SecureRandom();

    public String createTokenForUser(User user, int minutesValid) {
        String token = generateToken();
        PasswordResetToken entity = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(minutesValid, ChronoUnit.MINUTES))
                .used(false)
                .build();
        tokenRepo.save(entity);
        return token;
    }

    public User requireUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym e-mailu"));
    }

    public PasswordResetToken requireToken(String token) {
        return tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy token"));
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = requireToken(token);
        if (prt.isExpired()) throw new IllegalStateException("Token wygasł");
        if (prt.isUsed()) throw new IllegalStateException("Token został już użyty");

        User u = prt.getUser();
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(u);

        prt.setUsed(true);
        tokenRepo.save(prt);
    }

    private String generateToken() {
        byte[] buf = new byte[32];
        RANDOM.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

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
