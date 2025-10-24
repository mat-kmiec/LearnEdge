package pl.learnedge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.learnedge.dto.UpdateProfileDto;
import pl.learnedge.model.User;
import pl.learnedge.service.UserService;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profil")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        UpdateProfileDto profileDto = new UpdateProfileDto();
        profileDto.setFirstName(user.getFirstName());
        profileDto.setLastName(user.getLastName());
        profileDto.setEmail(user.getEmail());
        
        model.addAttribute("profile", profileDto);
        return "dashboard/profile";
    }

    @PostMapping("/profil")
    public String updateProfile(@AuthenticationPrincipal User user,
                              UpdateProfileDto profileDto,
                              RedirectAttributes ra) {
        try {
            User updatedUser = userService.updateProfile(user.getId(), profileDto);
            // Update the authentication object
            var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                updatedUser, user.getPassword(), user.getAuthorities()
            );
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
            ra.addFlashAttribute("success", "Profil został zaktualizowany.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profil";
    }
}
