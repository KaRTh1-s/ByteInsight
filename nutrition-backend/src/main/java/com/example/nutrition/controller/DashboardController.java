package com.example.nutrition.controller;

import com.example.nutrition.dto.DailySummaryDto;
import com.example.nutrition.service.MealJournalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final MealJournalService mealJournalService;

    public DashboardController(MealJournalService mealJournalService) {
        this.mealJournalService = mealJournalService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DailySummaryDto> getDashboardSummary(
            Authentication authentication,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        LocalDate targetDate = date != null ? date : LocalDate.now();
        DailySummaryDto summary = mealJournalService.getDailySummary(authentication.getName(), targetDate);
        return ResponseEntity.ok(summary);
    }
}
