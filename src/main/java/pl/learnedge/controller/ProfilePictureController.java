package pl.learnedge.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.learnedge.service.ProfilePictureService;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    @PostMapping("/picture")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file) {
        try {
            System.out.println("Received file upload request");
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());

            if (file.isEmpty()) {
                System.out.println("File is empty");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Plik jest pusty"));
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                System.out.println("Invalid content type: " + contentType);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nieprawidłowy format pliku. Dozwolone są tylko obrazy."));
            }
            String imageUrl = profilePictureService.saveProfilePicture(file);
            System.out.println("File saved successfully. URL: " + imageUrl);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Wystąpił błąd podczas przesyłania zdjęcia: " + e.getMessage()));
        }
    }
}