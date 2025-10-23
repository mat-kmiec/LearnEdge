package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.learnedge.model.PasswordResetToken;
import pl.learnedge.model.User;
import pl.learnedge.repository.PasswordResetTokenRepository;
import pl.learnedge.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final PasswordResetTokenRepository resetTokens;
    private final EmailService emailService;

    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (users.existsByUsername(username)) {
            throw new IllegalArgumentException("Użytkownik o takiej nazwie już istnieje");
        }
        if (email != null && !email.isBlank() && users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email jest już zajęty");
        }

        var user = User.builder()
                .username(username)
                .email(email)
                .password(encoder.encode(rawPassword))
                .role("ROLE_USER")
                .enabled(true)
                .build();

        return users.save(user);
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        var user = users.findByEmail(email)
                .orElse(null); // nie informujemy czy email istnieje

        if (user != null) {
            // Unieważnij poprzednie tokeny
            resetTokens.findByUserEmailOrderByCreatedAtDesc(email)
                    .ifPresent(token -> {
                        token.setUsed(true);
                        resetTokens.save(token);
                    });

            // Generuj nowy token
            var token = UUID.randomUUID().toString();
            var resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                    .used(false)
                    .build();

            resetTokens.save(resetToken);
            emailService.sendPasswordResetEmail(email, token);
        }
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        return resetTokens.findByToken(token)
                .filter(resetToken -> !resetToken.isExpired() && !resetToken.isUsed())
                .map(resetToken -> {
                    User user = resetToken.getUser();
                    user.setPassword(encoder.encode(newPassword));
                    resetToken.setUsed(true);
                    users.save(user);
                    resetTokens.save(resetToken);
                    return true;
                })
                .orElse(false);
    }
}