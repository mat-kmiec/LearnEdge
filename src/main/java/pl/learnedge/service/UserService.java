package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.learnedge.exception.EmailAlreadyTakenException;
import pl.learnedge.exception.UserAlreadyExistException;
import pl.learnedge.model.User;
import pl.learnedge.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (users.existsByUsername(username)) {
            throw new UserAlreadyExistException();
        }
        if (email != null && !email.isBlank() && users.existsByEmail(email)) {
            throw new EmailAlreadyTakenException();
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
}