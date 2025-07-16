package com.kma_backend.kma_backend.note;

import com.kma_backend.kma_backend.user.User;
import com.kma_backend.kma_backend.user.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdBy;
}
