package com.kma_backend.kma_backend.note;

import com.kma_backend.kma_backend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // 📥 Skapa en note (bara inloggad användare, gäller som ägare)
    @PostMapping("/create")
    public ResponseEntity<NoteDTO> createNote(@RequestBody CreateNoteDTO dto, Principal principal) {
        NoteDTO created = noteService.createNote(dto, principal.getName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 🔁 Uppdatera en note (endast ägare)
    @PutMapping("/update/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id,
                                              @RequestBody CreateNoteDTO dto,
                                              Principal principal) {
        NoteDTO updated = noteService.updateNote(id, dto, principal.getName());
        return ResponseEntity.ok(updated);
    }

    // 🗑️ Ta bort en note (endast ägare)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, Principal principal) {
        noteService.deleteNote(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // 📖 Hämta alla notes (synliga för alla inloggade)
    @GetMapping("/all")
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        List<NoteDTO> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }
}

