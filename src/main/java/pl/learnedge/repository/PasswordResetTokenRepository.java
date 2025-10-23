package pl.learnedge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.learnedge.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserEmailOrderByCreatedAtDesc(String email);
}
