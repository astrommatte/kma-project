package com.kma_backend.kma_backend.note;

import com.kma_backend.kma_backend.image.Image;
import com.kma_backend.kma_backend.mapper.DtoMapper;
import com.kma_backend.kma_backend.user.User;
import com.kma_backend.kma_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepo;
    private final UserRepository userRepo;
    private final DtoMapper dtoMapper;

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

        // Ta bort bildfilerna först – innan note tas bort
        for (Image image : note.getImages()) {
            try {
                Path path = Paths.get(image.getFilePath());
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Kunde inte ta bort fil från disk: " + image.getFilePath());
            }
        }

        // Sen tas noten + bilder bort från databasen (via cascade + orphanRemoval)
        noteRepo.delete(note);
    }



    public List<NoteDTO> getAllNotes() {
        return noteRepo.findAll().stream()
                .map(dtoMapper::toNoteDto)
                .toList();
    }

//    public NoteDTO toDto(Note note) {
//        NoteDTO dto = new NoteDTO();
//        dto.setId(note.getId());
//        dto.setTitle(note.getTitle());
//        dto.setContent(note.getContent());
//        dto.setCreatedAt(note.getCreatedAt());
//        dto.setUpdatedAt(note.getUpdatedAt());
//        dto.setCreatedBy(note.getCreatedBy());
//        return dto;
//    }

}

