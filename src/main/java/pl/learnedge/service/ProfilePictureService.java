package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.learnedge.model.User;
import pl.learnedge.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfilePictureService {

    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/profile-pictures}")
    private String uploadDir;

    public String saveProfilePicture(MultipartFile file) {
        try {
            System.out.println("Starting file save process");
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            System.out.println("Upload directory path: " + uploadPath);
            
            if (!Files.exists(uploadPath)) {
                System.out.println("Creating upload directory");
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            System.out.println("Original filename: " + originalFilename);
            
            String filename = UUID.randomUUID().toString() + getFileExtension(originalFilename);
            System.out.println("Generated filename: " + filename);
            
            Path filePath = uploadPath.resolve(filename);
            System.out.println("Full file path: " + filePath);

            // Save file
            try {
                Files.copy(file.getInputStream(), filePath);
                System.out.println("File saved successfully to disk");
            } catch (IOException e) {
                System.err.println("Error saving file: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Nie można zapisać pliku: " + e.getMessage());
            }

            // Update user profile picture in database
            User currentUser = getAuthenticatedUser();
            
            String profilePicturePath = "/uploads/profile-pictures/" + filename;
            System.out.println("Setting profile picture path: " + profilePicturePath);
            
            currentUser.setProfilePicture(profilePicturePath);
            userRepository.save(currentUser);
            System.out.println("User profile updated in database");

            return profilePicturePath;
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się zapisać zdjęcia profilowego", e);
        }
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

    private String getFileExtension(String filename) {
        if (filename == null) return ".jpg";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : ".jpg";
    }
}