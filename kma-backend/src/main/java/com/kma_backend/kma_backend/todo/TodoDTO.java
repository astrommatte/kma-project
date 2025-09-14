package com.kma_backend.kma_backend.todo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoDTO {
    private Long id;
    private String task;
    private boolean completed;
    private LocalDateTime createdAt;
}
