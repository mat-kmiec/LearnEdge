package pl.learnedge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.learnedge.dto.LessonDto;
import pl.learnedge.exception.LessonNotFoundException;
import pl.learnedge.mapper.LessonMapper;
import pl.learnedge.model.Course;
import pl.learnedge.model.Lesson;
import pl.learnedge.repository.CourseRepository;
import pl.learnedge.repository.LessonRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final CourseRepository courseRepository;

    public LessonDto getLessonBySlug(String slug) {
        Lesson lesson = lessonRepository.findBySlug(slug)
                .orElseThrow(LessonNotFoundException::new);
        return lessonMapper.toDto(lesson);
    }

    public void saveLesson(Long courseId,
                           String title,
                           String contentHtml,
                           List<MultipartFile> images,
                           List<MultipartFile> audioFiles) {

        // 🔹 1. Znajdź kurs
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kursu"));

        // 🔹 2. Utwórz obiekt lekcji
        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setCourse(course);
        lesson.setLessonOrder(0);
        lesson.setSlug(generateSlug(title));
        lesson.setContent(""); // tymczasowo, by spełnić NOT NULL
        lesson = lessonRepository.save(lesson);

        // 🔹 3. Utwórz docelowy katalog assets/{slug}/{lessonId}
        Path baseDir = Paths.get("src/main/resources/static/assets",
                course.getSlug(), lesson.getId().toString());
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Nie można utworzyć katalogu lekcji", e);
        }

        // 🔹 4. Zapisz pliki i zamień ścieżki w HTML
        Map<String, String> replacements = new HashMap<>();
        saveUploadedFiles(images, baseDir, replacements);
        saveUploadedFiles(audioFiles, baseDir, replacements);

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String oldName = entry.getKey();
            String newPath = "/assets/" + course.getSlug() + "/" + lesson.getId() + "/" + entry.getValue();

            // 🔹 zamień src="blob:..." lub src="oldName" na src="newPath"
            contentHtml = contentHtml.replaceAll(
                    "(?i)(src=\")([^\"]*" + oldName + ")(\")",
                    "$1" + newPath + "$3"
            );

            // 🔹 jeśli coś zostało z blobów — podmień też je globalnie
            contentHtml = contentHtml.replaceAll("(?i)blob:[a-z0-9\\-:/.]+", newPath);
        }


        // 🔹 5. Zapisz zaktualizowaną treść lekcji
        lesson.setContent(contentHtml);
        lessonRepository.save(lesson);
    }

    private void saveUploadedFiles(List<MultipartFile> files, Path baseDir, Map<String, String> replacements) {
        if (files == null) return;
        for (MultipartFile file : files) {
            try {
                String original = file.getOriginalFilename();
                if (original == null || original.isBlank()) {
                    original = "plik.mp3"; // lub plik.png
                }

                // zabezpieczenie przed ścieżkami
                original = Paths.get(original).getFileName().toString();

                // unikalna nazwa
                String fileName = UUID.randomUUID() + "-" + original;
                Path path = baseDir.resolve(fileName);

                file.transferTo(path);

                replacements.put(original, fileName);

                System.out.println("📥 Zapisano plik: " + fileName + " (" + file.getSize() + " B)");
            } catch (IOException e) {
                throw new RuntimeException("Błąd przy zapisie pliku: " + file.getOriginalFilename(), e);
            }
        }
    }

    private String generateSlug(String title) {
        if (title == null) return "";

        return title.toLowerCase()
                .replaceAll("ą", "a")
                .replaceAll("ć", "c")
                .replaceAll("ę", "e")
                .replaceAll("ł", "l")
                .replaceAll("ń", "n")
                .replaceAll("ó", "o")
                .replaceAll("ś", "s")
                .replaceAll("ź", "z")
                .replaceAll("ż", "z")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-");
    }
}
