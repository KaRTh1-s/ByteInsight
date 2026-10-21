package com.example.nutrition.controller;

import com.example.nutrition.entity.MealEntry;
import com.example.nutrition.service.MealJournalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
public class MealJournalController {

    private final MealJournalService mealJournalService;

    public MealJournalController(MealJournalService mealJournalService) {
        this.mealJournalService = mealJournalService;
    }

    @GetMapping
    public ResponseEntity<List<MealEntry>> getMeals(
            Authentication authentication,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (authentication == null) return ResponseEntity.status(401).build();

        LocalDate queryDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(mealJournalService.getMealsForDate(authentication.getName(), queryDate));
    }

    @PostMapping
    public ResponseEntity<?> addFoodToMeal(
            Authentication authentication,
            @RequestBody Map<String, Object> payload) {
        if (authentication == null) return ResponseEntity.status(401).build();

        String username = authentication.getName();
        String dateStr = (String) payload.get("date");
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
        String mealType = (String) payload.getOrDefault("mealType", payload.getOrDefault("mealCategory", "LUNCH"));

        // Support batch items payload
        Object itemsObj = payload.get("items");
        if (itemsObj instanceof java.util.List<?> itemsList && !itemsList.isEmpty()) {
            MealEntry lastEntry = null;
            for (Object obj : itemsList) {
                if (obj instanceof Map<?, ?> itemMap) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) itemMap;
                    String fName = (String) map.get("foodName");
                    if (fName == null || fName.isBlank()) continue;
                    Double portion = getDouble(map, "portion", 1.0);
                    String unit = (String) map.getOrDefault("unit", "serving");
                    Double calories = getDouble(map, "calories", 0.0);
                    Double protein = getDouble(map, "protein", 0.0);
                    Double fat = getDouble(map, "fat", 0.0);
                    Double carbs = getDouble(map, "carbs", 0.0);
                    Double fiber = getDouble(map, "fiber", 0.0);
                    Double sodium = getDouble(map, "sodium", 0.0);
                    Double potassium = getDouble(map, "potassium", 0.0);
                    Double calcium = getDouble(map, "calcium", 0.0);
                    Double iron = getDouble(map, "iron", 0.0);
                    Double cost = getDouble(map, "cost", 1.0);
                    lastEntry = mealJournalService.addFoodToMeal(
                            username, date, mealType, fName, portion, unit,
                            calories, protein, fat, carbs, fiber, sodium, potassium, calcium, iron, cost);
                }
            }
            return ResponseEntity.ok(lastEntry != null ? lastEntry : Map.of("message", "Items logged"));
        }

        String foodName = (String) payload.get("foodName");

        if (foodName == null || foodName.isBlank()) {
            return ResponseEntity.badRequest().body("Food name is required");
        }

        Double portion = getDouble(payload, "portion", 1.0);
        String unit = (String) payload.getOrDefault("unit", "serving");
        Double calories = getDouble(payload, "calories", 0.0);
        Double protein = getDouble(payload, "protein", 0.0);
        Double fat = getDouble(payload, "fat", 0.0);
        Double carbs = getDouble(payload, "carbs", 0.0);
        Double fiber = getDouble(payload, "fiber", 0.0);
        Double sodium = getDouble(payload, "sodium", 0.0);
        Double potassium = getDouble(payload, "potassium", 0.0);
        Double calcium = getDouble(payload, "calcium", 0.0);
        Double iron = getDouble(payload, "iron", 0.0);
        Double cost = getDouble(payload, "cost", 1.0);

        MealEntry updatedMeal = mealJournalService.addFoodToMeal(
                username, date, mealType, foodName, portion, unit,
                calories, protein, fat, carbs, fiber, sodium, potassium, calcium, iron, cost);

        return ResponseEntity.ok(updatedMeal);
    }

    @DeleteMapping("/{mealId}/items/{itemId}")
    public ResponseEntity<?> deleteMealItem(
            Authentication authentication,
            @PathVariable Long mealId,
            @PathVariable Long itemId) {
        if (authentication == null) return ResponseEntity.status(401).build();

        mealJournalService.deleteItem(authentication.getName(), mealId, itemId);
        return ResponseEntity.ok(Map.of("message", "Item deleted successfully"));
    }

    @DeleteMapping("/{mealId}")
    public ResponseEntity<?> deleteMeal(
            Authentication authentication,
            @PathVariable Long mealId) {
        if (authentication == null) return ResponseEntity.status(401).build();

        mealJournalService.deleteMeal(authentication.getName(), mealId);
        return ResponseEntity.ok(Map.of("message", "Meal deleted successfully"));
    }

    @PostMapping("/duplicate")
    public ResponseEntity<?> duplicateMeals(
            Authentication authentication,
            @RequestParam("sourceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sourceDate,
            @RequestParam("targetDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        if (authentication == null) return ResponseEntity.status(401).build();

        List<MealEntry> copied = mealJournalService.duplicateDayMeals(authentication.getName(), sourceDate, targetDate);
        return ResponseEntity.ok(copied);
    }

    private Double getDouble(Map<String, Object> map, String key, Double def) {
        Object val = map.get(key);
        if (val instanceof Number n) return n.doubleValue();
        if (val != null) {
            try { return Double.parseDouble(val.toString()); } catch (NumberFormatException ignored) {}
        }
        return def;
    }
}
