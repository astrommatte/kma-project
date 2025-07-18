package com.kma_backend.kma_backend.note;

import com.kma_backend.kma_backend.image.ImageDTO;
import com.kma_backend.kma_backend.user.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdBy;
    private List<ImageDTO> images;
}
