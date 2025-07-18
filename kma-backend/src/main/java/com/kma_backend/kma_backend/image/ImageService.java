package com.kma_backend.kma_backend.image;

import com.cloudinary.Cloudinary;
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
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final NoteRepository noteRepository;
    private final Cloudinary cloudinary;

    public Image uploadImage(MultipartFile file, Long noteId) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + noteId));

        Image image = new Image();
        image.setFileName(file.getOriginalFilename());
        image.setUrl(url);
        image.setPublicId(publicId);
        image.setNote(note);

        return imageRepository.save(image);
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + id));
    }

    public void deleteImage(Long id) throws IOException {
        Image image = getImageById(id);

        // Ta bort från Cloudinary
        try {
            cloudinary.uploader().destroy(image.getPublicId(), Map.of());
        } catch (Exception e) {
            throw new IOException("Failed to delete image from Cloudinary: " + e.getMessage());
        }

        // Ta bort från databasen
        imageRepository.delete(image);
    }
}

