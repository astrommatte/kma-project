package com.kma_backend.kma_backend.mapper;

import com.kma_backend.kma_backend.note.Note;
import com.kma_backend.kma_backend.note.NoteDTO;
import com.kma_backend.kma_backend.subscriber.Subscriber;
import com.kma_backend.kma_backend.subscriber.SubscriberDTO;
import com.kma_backend.kma_backend.user.User;
import com.kma_backend.kma_backend.user.UserDTO;
import com.kma_backend.kma_backend.user.UserRole;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public UserDTO toUserDto(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole() != null ? UserRole.valueOf(user.getRole().name()) : null);
        return dto;
    }

    public SubscriberDTO toSubscriberDto(Subscriber subscriber) {
        if (subscriber == null) return null;

        SubscriberDTO dto = new SubscriberDTO();
        dto.setId(subscriber.getId());
        dto.setFirstName(subscriber.getFirstName());
        dto.setLastName(subscriber.getLastName());
        dto.setEmail(subscriber.getEmail());
        dto.setType(subscriber.getType());

        // 💥 Här kan du nu anropa toUserDto
        dto.setOwner(toUserDto(subscriber.getOwner()));
        return dto;
    }

    public NoteDTO toNoteDto(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        // Mappa User entity till UserDTO
        dto.setCreatedBy(toUserDto(note.getCreatedBy()));
        return dto;
    }
}

