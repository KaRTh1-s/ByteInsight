package com.example.nutrition.controller;

import com.example.nutrition.entity.MealPlan;
import com.example.nutrition.entity.ShoppingListItem;
import com.example.nutrition.service.MealPlannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planner")
public class MealPlannerController {

    private final MealPlannerService mealPlannerService;

    public MealPlannerController(MealPlannerService mealPlannerService) {
        this.mealPlannerService = mealPlannerService;
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPlan(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        return mealPlannerService.getCurrentMealPlan(authentication.getName())
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    // Auto-generate a plan if user has none
                    MealPlan newPlan = mealPlannerService.generatePersonalizedPlan(authentication.getName(), "BALANCED", 7);
                    return ResponseEntity.ok(newPlan);
                });
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generatePlan(
            Authentication authentication,
            @RequestBody(required = false) Map<String, Object> body) {
        if (authentication == null) return ResponseEntity.status(401).build();

        String planType = body != null ? (String) body.getOrDefault("planType", "BALANCED") : "BALANCED";
        Integer days = body != null && body.get("days") instanceof Number n ? n.intValue() : 7;

        MealPlan plan = mealPlannerService.generatePersonalizedPlan(authentication.getName(), planType, days);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/to-shopping-list")
    public ResponseEntity<List<ShoppingListItem>> convertToShoppingList(
            Authentication authentication,
            @RequestBody Map<String, Object> body) {
        if (authentication == null) return ResponseEntity.status(401).build();

        Long planId = ((Number) body.get("mealPlanId")).longValue();
        List<ShoppingListItem> items = mealPlannerService.convertPlanToShoppingList(authentication.getName(), planId);
        return ResponseEntity.ok(items);
    }
}
