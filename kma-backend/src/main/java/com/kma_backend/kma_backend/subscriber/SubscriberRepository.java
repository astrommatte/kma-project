package com.kma_backend.kma_backend.subscriber;

import com.kma_backend.kma_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    List<Subscriber> findAllByOwnerId(Long ownerId);
    boolean existsByEmail(String email);
}
