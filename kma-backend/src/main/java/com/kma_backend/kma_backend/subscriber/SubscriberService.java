package com.kma_backend.kma_backend.subscriber;

import com.kma_backend.kma_backend.mapper.DtoMapper;
import com.kma_backend.kma_backend.user.User;
import com.kma_backend.kma_backend.user.UserDTO;
import com.kma_backend.kma_backend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private final UserRepository userRepository;
    private final SubscriberRepository subscriberRepo;
    private final DtoMapper dtoMapper;

    public SubscriberDTO createSubscriber(CreateSubscriberDTO createSubscriberDTO, String email) {

        if(subscriberRepo.existsByEmail(createSubscriberDTO.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subscriber subscriber = new Subscriber();

        subscriber.setEmail(createSubscriberDTO.getEmail());
        subscriber.setFirstName(createSubscriberDTO.getFirstName());
        subscriber.setLastName(createSubscriberDTO.getLastName());
        subscriber.setOwner(owner); // 💥 viktigt!
        subscriber.setType(createSubscriberDTO.getType());

        Subscriber savedSubscriber = subscriberRepo.save(subscriber);

        return dtoMapper.toSubscriberDto(savedSubscriber);
    }


    @Transactional
    public SubscriberDTO updateSubscriber(Long id, CreateSubscriberDTO dto, String currentUserEmail, String newOwnerEmail) {
        Subscriber subscriber = subscriberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        if (subscriber.getOwner() == null) {
            throw new RuntimeException("Subscriber has no owner");
        }

        if (!subscriber.getOwner().getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new RuntimeException("You are not the owner of this subscriber");
        }
        System.out.println("newOwnerEmail = " + newOwnerEmail);
        System.out.println("currentUserEmail = " + currentUserEmail);

        if (newOwnerEmail != null && !newOwnerEmail.isEmpty() && !newOwnerEmail.equals(currentUserEmail)) {
            User newOwner = userRepository.findByEmail(newOwnerEmail)
                    .orElseThrow(() -> new RuntimeException("New owner not found"));
            subscriber.setOwner(newOwner);
        }

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(subscriber.getEmail())) {
            if (subscriberRepo.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already exists.");
            }
            subscriber.setEmail(dto.getEmail());
        }

        subscriber.setFirstName(dto.getFirstName());
        subscriber.setLastName(dto.getLastName());

        subscriber.setType(dto.getType());

        Subscriber updated = subscriberRepo.save(subscriber);
        return dtoMapper.toSubscriberDto(updated);
    }


    public List<SubscriberDTO> getAllSubscribers() {

        List<Subscriber> subscribers = subscriberRepo.findAll();

        return subscribers.stream()
                .map(dtoMapper::toSubscriberDto)
                .toList();
    }

    public List<SubscriberDTO> getSubscribersByOwnerId(Long ownerId) {
        List<Subscriber> subs = subscriberRepo.findAllByOwnerId(ownerId);
        return subs.stream().map(dtoMapper::toSubscriberDto).collect(Collectors.toList());
    }

    public void deleteSubscriber(Long id, String currentUserEmail) {
        Subscriber subscriber = subscriberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        if (!subscriber.getOwner().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You are not the owner of this subscriber");
        }

        subscriberRepo.delete(subscriber);
    }

//    public SubscriberDTO toDto(Subscriber subscriber) {
//        SubscriberDTO dto = new SubscriberDTO();
//        dto.setId(subscriber.getId());
//        dto.setFirstName(subscriber.getFirstName());
//        dto.setLastName(subscriber.getLastName());
//        dto.setEmail(subscriber.getEmail());
//        dto.setType(subscriber.getType());
//
//        // ✅ Använd toUserDto här!
//        dto.setOwner(toUserDto(subscriber.getOwner()));
//
//        return dto;
//    }


    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No authenticated user");
    }
}
