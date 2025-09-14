package com.kma_backend.kma_backend.todo;

import com.kma_backend.kma_backend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(
            @Valid @RequestBody CreateTodoDTO dto,Principal principal) {
        return ResponseEntity.ok(todoService.createTodo(dto, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getTodos(
            Principal principal) {
        return ResponseEntity.ok(todoService.getTodosForUser(principal.getName()));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TodoDTO> markCompleted(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(todoService.markCompleted(id, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long id,
            Principal principal) {
        todoService.deleteTodo(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
