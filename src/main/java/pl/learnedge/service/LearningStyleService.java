package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.learnedge.model.User;
import pl.learnedge.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningStyleService {

    private final UserRepository userRepository;
    private final HuggingFaceAiService aiService;

    public void saveLearningStyle(String learningStyle) {
        User currentUser = getAuthenticatedUser();
        
        // Validate learning style
        if (learningStyle == null || !isValidLearningStyle(learningStyle)) {
            throw new IllegalArgumentException("Nieprawidłowy styl uczenia się");
        }
        
        currentUser.setLearningStyle(learningStyle);
        userRepository.save(currentUser);
        
        // Update the authentication object in session
        updateAuthenticationObject(currentUser);
        
        log.info("Zapisano styl uczenia się {} dla użytkownika {}", 
                 learningStyle, currentUser.getUsername());
    }

    /**
     * Analizuje odpowiedzi z ankiety i automatycznie określa styl uczenia
     */
    public String analyzeAndSaveLearningStyle(Map<String, String> surveyAnswers) {
        try {
            String analyzedStyle = aiService.analyzeLearningStyle(surveyAnswers);
            
            // Zapisz przeanalizowany styl
            User currentUser = getAuthenticatedUser();
            currentUser.setLearningStyle(analyzedStyle);
            userRepository.save(currentUser);
            
            // Update the authentication object in session
            updateAuthenticationObject(currentUser);
            
            log.info("AI przeanalizowało styl uczenia dla użytkownika {}: {}", 
                     currentUser.getUsername(), analyzedStyle);
            
            return analyzedStyle;
        } catch (Exception e) {
            log.error("Błąd podczas analizy stylu uczenia: ", e);
            throw new RuntimeException("Nie udało się przeanalizować stylu uczenia", e);
        }
    }

    /**
     * Sprawdza czy AI jest dostępne do analizy
     */
    public boolean isAiAnalysisAvailable() {
        return aiService.isAvailable();
    }

    private boolean isValidLearningStyle(String style) {
        return style.equals("VISUAL") || style.equals("AUDITORY") || 
               style.equals("KINESTHETIC") || style.equals("MIXED");
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

    private void updateAuthenticationObject(User updatedUser) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null) {
            // Create new authentication with updated user
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser, 
                currentAuth.getCredentials(), 
                currentAuth.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
