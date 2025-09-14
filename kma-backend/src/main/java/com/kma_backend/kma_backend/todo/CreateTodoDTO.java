package com.kma_backend.kma_backend.todo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTodoDTO {
    @NotBlank
    private String task;
    private LocalDateTime dueDate;
}
