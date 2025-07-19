package com.kma_backend.kma_backend.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/registration")
    public ResponseEntity<Map<String, Boolean>> isRegistrationAllowed() {
        boolean allowed = settingService.isUserRegistrationAllowed();
        return ResponseEntity.ok(Collections.singletonMap("allowed", allowed));
    }

    @PutMapping("/registration")
    public ResponseEntity<Void> updateRegistrationSetting(@RequestBody Map<String, Boolean> body) {
        boolean allow = body.getOrDefault("allowed", false);
        settingService.updateAllowUserRegistration(allow);
        return ResponseEntity.ok().build();
    }

}
