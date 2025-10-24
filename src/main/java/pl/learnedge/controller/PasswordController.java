package pl.learnedge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.learnedge.dto.ChangePasswordRequest;
import pl.learnedge.service.PasswordService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            passwordService.changePassword(request.getCurrentPassword(), request.getNewPassword());
            Map<String, String> body = new HashMap<>();
            body.put("success", "Hasło zostało zmienione");
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            log.warn("Change password validation error: {}", e.getMessage());
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(body);
        } catch (Exception e) {
            log.error("Change password error: ", e);
            Map<String, String> body = new HashMap<>();
            body.put("error", "Wystąpił błąd podczas zmiany hasła");
            return ResponseEntity.internalServerError().body(body);
        }
    }
}
