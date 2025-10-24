package pl.learnedge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.learnedge.service.LearningStyleService;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class LearningStyleController {

    private final LearningStyleService learningStyleService;

    @PostMapping("/learning-style")
    public ResponseEntity<Map<String, String>> saveLearningStyle(@RequestBody Map<String, String> request) {
        try {
            String learningStyle = request.get("learningStyle");
            
            if (learningStyle == null || learningStyle.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Wybierz styl uczenia się"));
            }
            
            learningStyleService.saveLearningStyle(learningStyle);
            
            return ResponseEntity.ok(Map.of(
                "message", "Styl uczenia się został zapisany",
                "learningStyle", learningStyle
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Wystąpił błąd podczas zapisywania stylu uczenia się"));
        }
    }
}
