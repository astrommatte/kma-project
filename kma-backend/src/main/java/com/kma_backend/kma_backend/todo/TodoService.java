package com.kma_backend.kma_backend.todo;

import com.kma_backend.kma_backend.mapper.DtoMapper;
import com.kma_backend.kma_backend.user.User;
import com.kma_backend.kma_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final DtoMapper dtoMapper;
    private final UserRepository userRepo;

    public TodoDTO createTodo(CreateTodoDTO dto, String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Todo todo = new Todo();
        todo.setTask(dto.getTask());
        todo.setUser(user);

        Todo saved = todoRepository.save(todo);

        return dtoMapper.toTodoDTO(saved);
    }

    public List<TodoDTO> getTodosForUser(String userEmail) {
        User todoOwner = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return todoRepository.findByUser(todoOwner)
                .stream()
                .map(dtoMapper::toTodoDTO)
                .toList();
    }

    public TodoDTO markCompleted(Long id, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        User todoOwner = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!todo.getUser().getId().equals(todoOwner.getId())) {
            throw new RuntimeException("Not allowed");
        }

        if(!todo.isCompleted()){
            todo.setCompleted(true);
        }
        else {
            todo.setCompleted(false);
        }

        return dtoMapper.toTodoDTO(todoRepository.save(todo));
    }

    public void deleteTodo(Long id, String userEmail) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        User todoOwner = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!todo.getUser().getId().equals(todoOwner.getId())) {
            throw new RuntimeException("Not allowed");
        }

        todoRepository.delete(todo);
    }
}
