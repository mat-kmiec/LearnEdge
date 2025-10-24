package pl.learnedge.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.learnedge.model.User;
import pl.learnedge.service.MailService;
import pl.learnedge.service.PasswordResetService;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService resetService;
    private final MailService mailService;

    @GetMapping("/przypomnij-haslo")
    public String forgotPassword(@RequestParam(value = "sent", required = false) String sent,
                                 Model model) {
        model.addAttribute("sent", sent != null);
        return "home/forgot-password";
    }

    @PostMapping("/przypomnij-haslo")
    public String sendReset(@RequestParam String email,
                            HttpServletRequest request,
                            RedirectAttributes ra) {
        try {
            User user = resetService.requireUserByEmail(email);
            String token = resetService.createTokenForUser(user, 30);
            String link = getAppUrl(request) + "/reset-hasla?token=" + token;

            // Wysyłka maila — jeśli tu poleci wyjątek (np. SMTP), UI nadal pokaże sukces
            mailService.send(email, "Reset hasła – LearnEdge",
                    "Cześć!\n\nKliknij, aby ustawić nowe hasło:\n" + link + "\n");
        } catch (Exception ignored) {
            // Nie ujawniamy szczegółów użytkownikowi (bezpieczeństwo/UX)
        }
        ra.addAttribute("sent", "true");
        return "redirect:/przypomnij-haslo";
    }

    @GetMapping("/reset-hasla")
    public String resetForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "home/reset-password";
    }

    @PostMapping("/reset-hasla")
    public String doReset(@RequestParam String token,
                          @RequestParam String password,
                          RedirectAttributes ra) {
        try {
            resetService.resetPassword(token, password);
            ra.addFlashAttribute("info", "Hasło zmienione. Zaloguj się nowym hasłem.");
            return "redirect:/logowanie";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reset-hasla?token=" + token;
        }
    }

    private String getAppUrl(HttpServletRequest req) {
        String scheme = req.getHeader("X-Forwarded-Proto");
        if (scheme == null) scheme = req.getScheme();
        String host = req.getHeader("X-Forwarded-Host");
        if (host == null) {
            int port = req.getServerPort();
            String portPart = (port == 80 || port == 443) ? "" : ":" + port;
            host = req.getServerName() + portPart;
        }
        return scheme + "://" + host;
    }
}
