package pl.learnedge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.learnedge.service.LearningStyleService;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AiSurveyController {

    private final LearningStyleService learningStyleService;

    @GetMapping("/ankieta")
    public String showSurvey(Model model) {
        // Sprawdź czy AI jest dostępne
        boolean aiAvailable = learningStyleService.isAiAnalysisAvailable();
        model.addAttribute("aiAvailable", aiAvailable);

        if (!aiAvailable) {
            model.addAttribute("message", "Analiza AI jest obecnie niedostępna. Możesz wybrać styl uczenia ręcznie.");
        }

        return "dashboard/ankieta";
    }

    @PostMapping("/api/survey/analyze")
    @ResponseBody
    public ResponseEntity<?> analyzeSurvey(@RequestBody Map<String, String> surveyAnswers) {
        try {
            // Sprawdź czy AI jest dostępne
            if (!learningStyleService.isAiAnalysisAvailable()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Analiza AI jest obecnie niedostępna"));
            }

            // Analizuj odpowiedzi
            String learningStyle = learningStyleService.analyzeAndSaveLearningStyle(surveyAnswers);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "learningStyle", learningStyle,
                "message", "Twój styl uczenia został przeanalizowany: " + translateStyle(learningStyle)
            ));

        } catch (Exception e) {
            log.error("Błąd podczas analizy ankiety: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Wystąpił błąd podczas analizy"));
        }
    }

    @PostMapping("/api/survey/manual")
    @ResponseBody
    public ResponseEntity<?> saveManualSelection(@RequestBody Map<String, String> request) {
        try {
            String selectedStyle = request.get("learningStyle");
            learningStyleService.saveLearningStyle(selectedStyle);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Styl uczenia został zapisany: " + translateStyle(selectedStyle)
            ));

        } catch (Exception e) {
            log.error("Błąd podczas zapisywania stylu uczenia: ", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Wystąpił błąd podczas zapisywania"));
        }
    }

    private String translateStyle(String style) {
        return switch (style) {
            case "VISUAL" -> "Wzrokowy";
            case "AUDITORY" -> "Słuchowy";
            case "KINESTHETIC" -> "Kinestetyczny";
            case "MIXED" -> "Mieszany";
            default -> style;
        };
    }
}