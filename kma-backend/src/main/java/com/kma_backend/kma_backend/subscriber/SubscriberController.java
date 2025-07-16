package com.kma_backend.kma_backend.subscriber;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/subscribers")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriberService subscriberService;

    // 📌 CREATE
    @PostMapping("/create")
    public ResponseEntity<SubscriberDTO> createSubscriber(
            @RequestBody CreateSubscriberDTO dto,
            Principal principal) {

        SubscriberDTO created = subscriberService.createSubscriber(dto, principal.getName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<SubscriberDTO>> getAllSubscribers() {
        List<SubscriberDTO> subscribers = subscriberService.getAllSubscribers();
        return ResponseEntity.ok(subscribers);
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<List<SubscriberDTO>> getSubscribersByOwner(@PathVariable Long ownerId) {
        List<SubscriberDTO> subscribers = subscriberService.getSubscribersByOwnerId(ownerId);
        return ResponseEntity.ok(subscribers);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SubscriberDTO> updateSubscriber(
            @PathVariable Long id,
            @RequestBody CreateSubscriberDTO dto,
            Principal principal) {

        SubscriberDTO updated = subscriberService.updateSubscriber(
                id,
                dto,
                principal.getName(),
                dto.getNewOwnerEmail()
        );

        return ResponseEntity.ok(updated);
    }

    // 🗑 DELETE (bara nuvarande ägaren får ta bort)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubscriber(
            @PathVariable Long id,
            Principal principal) {

        subscriberService.deleteSubscriber(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
