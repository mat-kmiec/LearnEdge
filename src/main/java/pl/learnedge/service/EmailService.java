package pl.learnedge.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String from;

    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("resetToken", resetToken);
            context.setVariable("resetLink", "http://localhost:8080/reset-password?token=" + resetToken);
            
            String content = templateEngine.process("email/reset-password", context);
            
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject("Reset hasła - LearnEdge");
            helper.setText(content, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Błąd podczas wysyłania emaila", e);
        }
    }

    public void send(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        if (from != null && !from.isBlank()) {
            msg.setFrom(from); // Gmail i część SMTP tego wymaga
        }
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}