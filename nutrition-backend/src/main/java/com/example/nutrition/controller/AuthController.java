package com.example.nutrition.controller;

import com.example.nutrition.entity.User;
import com.example.nutrition.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().length() < 3) {
                return ResponseEntity.badRequest().body("Username must be at least 3 characters.");
            }
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body("Password must be at least 6 characters.");
            }
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                return ResponseEntity.badRequest().body("Valid email is required.");
            }

            User saved = userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
            Map<String, Object> authData = userService.loginAndGenerateToken(saved.getUsername(), user.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("username", saved.getUsername());
            response.put("token", authData.get("token"));
            response.put("profile", authData.get("profile"));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Registration error: " + ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required.");
            }

            Map<String, Object> authData = userService.loginAndGenerateToken(user.getUsername(), user.getPassword());
            return ResponseEntity.ok(authData);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Login error: " + ex.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }
}
