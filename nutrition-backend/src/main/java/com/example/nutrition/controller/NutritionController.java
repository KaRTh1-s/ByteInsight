package com.example.nutrition.controller;

import com.example.nutrition.dto.MealAnalysisRequest;
import com.example.nutrition.dto.MealAnalysisResult;
import com.example.nutrition.entity.FoodItem;
import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.service.FoodService;
import com.example.nutrition.service.FoodVisionService;
import com.example.nutrition.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NutritionController {

    private final FoodService foodService;
    private final FoodVisionService foodVisionService;
    private final UserService userService;

    public NutritionController(FoodService foodService,
                               FoodVisionService foodVisionService,
                               UserService userService) {
        this.foodService = foodService;
        this.foodVisionService = foodVisionService;
        this.userService = userService;
    }

    /**
     * Preserves exact backward compatibility with existing POST /api/nutrition
     */
    @PostMapping("/nutrition")
    public ResponseEntity<Object> getNutrition(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty");
        }

        try {
            List<Map<String, Object>> items = foodService.queryFoodNutrition(query);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch data: " + e.getMessage());
        }
    }

    @GetMapping("/foods/search")
    public ResponseEntity<List<FoodItem>> searchFoods(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(foodService.searchFoods(query));
    }

    @GetMapping("/foods/{id}")
    public ResponseEntity<?> getFoodById(@PathVariable Long id) {
        return foodService.getFoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/foods/analyze")
    public ResponseEntity<MealAnalysisResult> analyzeMeal(@RequestBody MealAnalysisRequest request,
                                                         Authentication authentication) {
        UserProfile profile = null;
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                profile = userService.getUserProfile(authentication.getName());
            } catch (Exception ignored) {}
        }
        MealAnalysisResult result = foodService.analyzeMeal(request, profile);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/foods/analyze-image")
    public ResponseEntity<?> analyzeFoodImage(@RequestParam("image") MultipartFile image) {
        try {
            Map<String, Object> visionResult = foodVisionService.analyzeFoodImage(image);
            return ResponseEntity.ok(visionResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", "Image analysis failed: " + e.getMessage()));
        }
    }
}
