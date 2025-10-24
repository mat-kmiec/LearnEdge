package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.learnedge.model.User;
import pl.learnedge.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LearningStyleService {

    private final UserRepository userRepository;

    public void saveLearningStyle(String learningStyle) {
        User currentUser = getAuthenticatedUser();
        
        // Validate learning style
        if (learningStyle == null || !isValidLearningStyle(learningStyle)) {
            throw new IllegalArgumentException("Nieprawidłowy styl uczenia się");
        }
        
        currentUser.setLearningStyle(learningStyle);
        userRepository.save(currentUser);
    }

    private boolean isValidLearningStyle(String style) {
        return style.equals("VISUAL") || style.equals("AUDITORY") || style.equals("KINESTHETIC");
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Brak zalogowanego użytkownika");
        }
        String username;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            username = ud.getUsername();
        } else if (principal instanceof User u) {
            username = u.getUsername();
        } else {
            username = auth.getName();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie został znaleziony: " + username));
    }
}
