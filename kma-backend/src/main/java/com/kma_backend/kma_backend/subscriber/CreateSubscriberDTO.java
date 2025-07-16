package com.kma_backend.kma_backend.subscriber;

import com.kma_backend.kma_backend.user.UserDTO;
import lombok.Data;

@Data
public class CreateSubscriberDTO {
    private String firstName;
    private String lastName;
    private String email;
    private UserDTO owner;
    private String newOwnerEmail;
    private SubscriberType type;
}
