package com.kma_backend.kma_backend.note;

import com.cloudinary.Cloudinary;
import com.kma_backend.kma_backend.image.Image;
import com.kma_backend.kma_backend.mapper.DtoMapper;
import com.kma_backend.kma_backend.user.User;
import com.kma_backend.kma_backend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepo;
    private final UserRepository userRepo;
    private final DtoMapper dtoMapper;
    private final Cloudinary cloudinary;

    public NoteDTO createNote(CreateNoteDTO dto, String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setCreatedBy(user);
        //note.setCreatedAt(LocalDateTime.now());

        Note saved = noteRepo.save(note);

        return dtoMapper.toNoteDto(saved);
    }

    public NoteDTO updateNote(Long id, CreateNoteDTO dto, String userEmail) {
        Note note = noteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getCreatedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not the owner of this note");
        }

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        //note.setUpdatedAt(LocalDateTime.now());

        Note updatedNote = noteRepo.save(note);

        return dtoMapper.toNoteDto(updatedNote);
    }

    public void deleteNote(Long id, String userEmail) {
        Note note = noteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getCreatedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not the owner of this note");
        }

        // Radera bilder från Cloudinary
        for (Image image : note.getImages()) {
            try {
                cloudinary.uploader().destroy(image.getPublicId(), Map.of());
            } catch (IOException e) {
                System.err.println("Kunde inte ta bort bild från Cloudinary: " + image.getPublicId());
            }
        }

        // Radera noten och bilder från DB (via cascade)
        noteRepo.delete(note);
    }

    @Transactional
    public List<NoteDTO> getAllNotes() {
        return noteRepo.findAll().stream()
                .map(dtoMapper::toNoteDto)
                .toList();
    }

}

