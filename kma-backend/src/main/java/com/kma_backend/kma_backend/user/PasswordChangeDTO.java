package com.kma_backend.kma_backend.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PasswordChangeDTO {
    private String newPassword;
}

