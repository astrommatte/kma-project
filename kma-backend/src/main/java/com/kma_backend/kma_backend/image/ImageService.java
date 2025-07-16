package com.kma_backend.kma_backend.image;

import com.kma_backend.kma_backend.note.Note;
import com.kma_backend.kma_backend.note.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class ImageService {

    @Value("${app.image.upload-dir}")
    private String uploadDir;
    private final ImageRepository imageRepository;
    private final NoteRepository noteRepository;


    public Image saveImage(MultipartFile file, Long noteId) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String newFileName = UUID.randomUUID() + "_" + originalFileName;
        Path targetPath = Paths.get(uploadDir).resolve(newFileName).normalize();

        // Skapa mappen om den inte finns
        Files.createDirectories(targetPath.getParent());

        // Spara filen
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Koppla till note
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + noteId));

        Image image = new Image();
        image.setFileName(originalFileName);
        image.setFilePath(targetPath.toString());
        image.setNote(note);

        return imageRepository.save(image);
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + id));
    }

    public void deleteImage(Long id) throws IOException {
        Image image = getImageById(id);

        // Ta bort filen från disk
        Path path = Paths.get(image.getFilePath());
        Files.deleteIfExists(path);

        // Ta bort från databasen
        imageRepository.delete(image);
    }
}
