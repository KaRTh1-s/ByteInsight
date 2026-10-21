package com.example.nutrition.controller;

import com.example.nutrition.entity.User;
import com.example.nutrition.entity.WeightLog;
import com.example.nutrition.repository.UserRepository;
import com.example.nutrition.repository.WeightLogRepository;
import com.example.nutrition.service.MealJournalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalsController {

    private final WeightLogRepository weightLogRepository;
    private final UserRepository userRepository;
    private final MealJournalService mealJournalService;

    public GoalsController(WeightLogRepository weightLogRepository,
                           UserRepository userRepository,
                           MealJournalService mealJournalService) {
        this.weightLogRepository = weightLogRepository;
        this.userRepository = userRepository;
        this.mealJournalService = mealJournalService;
    }

    @GetMapping("/weight")
    public ResponseEntity<List<WeightLog>> getWeightHistory(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(weightLogRepository.findByUserUsernameOrderByLogDateAsc(authentication.getName()));
    }

    @PostMapping("/weight")
    public ResponseEntity<?> logWeight(
            Authentication authentication,
            @RequestBody Map<String, Object> body) {
        if (authentication == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Double weightKg = ((Number) body.get("weightKg")).doubleValue();
        String dateStr = (String) body.get("date");
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
        String note = (String) body.getOrDefault("note", "");

        WeightLog log = new WeightLog(user, date, weightKg, note);
        return ResponseEntity.ok(weightLogRepository.save(log));
    }
}
