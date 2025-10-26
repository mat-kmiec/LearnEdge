package pl.learnedge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HuggingFaceAiService {

    @Value("${app.ai.huggingface.token}")
    private String token;
    
    @Value("${app.ai.huggingface.model}")
    private String model;
    
    @Value("${app.ai.huggingface.api-url}")
    private String apiUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Analizuje odpowiedzi ankiety i określa styl uczenia się
     */
    public String analyzeLearningStyle(Map<String, String> surveyAnswers) {
        try {
            // Najpierw spróbuj analizy słów kluczowych (bardziej niezawodne)
            String keywordResult = analyzeByKeywords(surveyAnswers);
            if (!"MIXED".equals(keywordResult)) {
                log.info("Analiza słów kluczowych wskazała: {}", keywordResult);
                return keywordResult;
            }
            
            // Jeśli analiza słów kluczowych nie dała wyniku, użyj AI jako backup
            String prompt = buildAnalysisPrompt(surveyAnswers);
            String aiResult = callHuggingFaceApi(prompt);
            log.info("Analiza AI wskazała: {}", aiResult);
            return extractLearningStyle(aiResult);
        } catch (Exception e) {
            log.error("Błąd podczas analizy stylu uczenia: ", e);
            return "MIXED"; // Fallback
        }
    }

    private String analyzeByKeywords(Map<String, String> answers) {
        String allText = String.join(" ", answers.values()).toLowerCase();
        
        int visualScore = 0;
        int auditoryScore = 0;
        int kinestheticScore = 0;
        
        // Słowa kluczowe dla stylu wzrokowego
        String[] visualKeywords = {"wzrokowo", "diagram", "mapa", "kolorowe", "infografik", 
                                 "slajdy", "schemat", "rysunk", "ikon", "visual", "obrazy", 
                                 "kolorami", "zaznaczam", "widzę", "patrzę", "obserwuję"};
        
        // Słowa kluczowe dla stylu słuchowego  
        String[] auditoryKeywords = {"słucham", "słyszę", "mówię", "dyskusja", "rozmowa", 
                                    "muzyka", "nagrania", "audio", "głos", "dźwięk", 
                                    "wyjaśniam", "powtarzam", "rytm", "melodia"};
        
        // Słowa kluczowe dla stylu kinestetycznego
        String[] kinestheticKeywords = {"ruch", "dotyk", "praktyka", "ćwiczenia", "aktywność", 
                                      "fizyczne", "doświadczenie", "robię", "buduję", "eksperyment",
                                      "manipulacja", "hands-on", "kinesthetic", "sportowe"};
        
        // Liczenie wystąpień słów kluczowych
        for (String keyword : visualKeywords) {
            if (allText.contains(keyword)) {
                visualScore++;
                log.debug("Znaleziono słowo wizualne: {}", keyword);
            }
        }
        
        for (String keyword : auditoryKeywords) {
            if (allText.contains(keyword)) {
                auditoryScore++;
                log.debug("Znaleziono słowo słuchowe: {}", keyword);
            }
        }
        
        for (String keyword : kinestheticKeywords) {
            if (allText.contains(keyword)) {
                kinestheticScore++;
                log.debug("Znaleziono słowo kinestetyczne: {}", keyword);
            }
        }
        
        log.info("Wyniki analizy słów kluczowych - Visual: {}, Auditory: {}, Kinesthetic: {}", 
                visualScore, auditoryScore, kinestheticScore);
        
        // Określenie wyniku na podstawie najwyższego wyniku
        if (visualScore > auditoryScore && visualScore > kinestheticScore) {
            return "VISUAL";
        } else if (auditoryScore > visualScore && auditoryScore > kinestheticScore) {
            return "AUDITORY"; 
        } else if (kinestheticScore > visualScore && kinestheticScore > auditoryScore) {
            return "KINESTHETIC";
        }
        
        // Jeśli wyniki są równe lub wszystkie zerowe, zwróć MIXED
        return "MIXED";
    }

    private String buildAnalysisPrompt(Map<String, String> answers) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze these learning preferences and determine if the person is: VISUAL, AUDITORY, or KINESTHETIC learner.\n\n");
        
        answers.forEach((question, answer) -> 
            prompt.append("Q: ").append(question).append("\nA: ").append(answer).append("\n\n")
        );
        
        prompt.append("Based on these answers, this person is primarily a:");
        return prompt.toString();
    }

    private String callHuggingFaceApi(String prompt) {
        log.info("Wysyłam prompt do Hugging Face API: {}", prompt);
        
        WebClient webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        // Używamy BART do klasyfikacji tekstu
        Map<String, Object> requestBody = Map.of(
            "inputs", prompt,
            "parameters", Map.of(
                "candidate_labels", List.of("VISUAL", "AUDITORY", "KINESTHETIC"),
                "multi_label", false
            )
        );

        log.info("Request body: {}", requestBody);

        try {
            Mono<Map> response = webClient.post()
                    .uri("/" + model)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class);

            Map<String, Object> result = response.block();
            log.info("Odpowiedź z Hugging Face API: {}", result);
            
            if (result != null && result.containsKey("labels")) {
                List<String> labels = (List<String>) result.get("labels");
                List<Double> scores = (List<Double>) result.get("scores");
                log.info("Labels: {}, Scores: {}", labels, scores);
                return labels.get(0); // Najbardziej prawdopodobna etykieta
            }
            
            log.warn("Nieprawidłowa odpowiedź z API - brak labels");
            return "MIXED";
        } catch (Exception e) {
            log.error("Błąd wywołania Hugging Face API: ", e);
            return "MIXED";
        }
    }

    private String extractLearningStyle(String apiResult) {
        // Dla wyników z nowego API (bezpośrednio etykieta)
        if (apiResult.toUpperCase().contains("VISUAL")) {
            return "VISUAL";
        } else if (apiResult.toUpperCase().contains("AUDITORY")) {
            return "AUDITORY";
        } else if (apiResult.toUpperCase().contains("KINESTHETIC")) {
            return "KINESTHETIC";
        }
        return "MIXED";
    }

    /**
     * Sprawdza czy usługa AI jest dostępna
     */
    public boolean isAvailable() {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(apiUrl)
                    .defaultHeader("Authorization", "Bearer " + token)
                    .build();

            Map<String, Object> testRequest = Map.of(
                "inputs", "test",
                "parameters", Map.of("candidate_labels", List.of("test"))
            );

            Mono<Map> response = webClient.post()
                    .uri("/" + model)
                    .bodyValue(testRequest)
                    .retrieve()
                    .bodyToMono(Map.class);

            response.block();
            return true;
        } catch (Exception e) {
            log.warn("Hugging Face API niedostępne: ", e);
            return false;
        }
    }
}