package com.example.nutrition.controller;

import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication required.");
        }
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UserProfile profile) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication required.");
        }
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateUserProfile(username, profile));
    }
}
