package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
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
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

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
        return userRepo.findByEmail(email)
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
}
