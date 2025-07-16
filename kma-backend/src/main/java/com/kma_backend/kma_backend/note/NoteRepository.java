package com.kma_backend.kma_backend.note;

import com.kma_backend.kma_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByCreatedBy(User user);
}

