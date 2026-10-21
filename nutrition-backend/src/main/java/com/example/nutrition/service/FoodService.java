package com.example.nutrition.service;

import com.example.nutrition.dto.FoodAnalysisItemDto;
import com.example.nutrition.dto.MealAnalysisRequest;
import com.example.nutrition.dto.MealAnalysisResult;
import com.example.nutrition.entity.FoodItem;
import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FoodService {

    private static final Logger logger = LoggerFactory.getLogger(FoodService.class);

    private final FoodItemRepository foodItemRepository;
    private final NutritionIntelligenceService nutritionIntelligenceService;
    private final String ninjaApiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    // In-memory cache for external nutrition queries to reduce network trips and rate limiting
    private final Map<String, List<Map<String, Object>>> apiCache = new ConcurrentHashMap<>();

    public FoodService(FoodItemRepository foodItemRepository,
                       NutritionIntelligenceService nutritionIntelligenceService,
                       @Value("${nutrition.ninja.api.key:}") String ninjaApiKey) {
        this.foodItemRepository = foodItemRepository;
        this.nutritionIntelligenceService = nutritionIntelligenceService;
        this.ninjaApiKey = ninjaApiKey;
    }

    public List<FoodItem> searchFoods(String query) {
        if (query == null || query.trim().isEmpty()) {
            return foodItemRepository.findAll();
        }
        return foodItemRepository.searchFoods(query.trim());
    }

    public Optional<FoodItem> getFoodById(Long id) {
        return foodItemRepository.findById(id);
    }

    /**
     * Resolves food details either from verified local database or external API.
     */
    public List<Map<String, Object>> queryFoodNutrition(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String cleanQuery = query.trim().toLowerCase();

        // 1. Check local verified database first
        List<FoodItem> localMatches = foodItemRepository.searchFoods(cleanQuery);
        if (!localMatches.isEmpty()) {
            List<Map<String, Object>> results = new ArrayList<>();
            for (FoodItem item : localMatches) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", item.getName());
                map.put("calories", item.getCalories());
                map.put("protein_g", item.getProteinG());
                map.put("fat_total_g", item.getFatTotalG());
                map.put("carbohydrates_total_g", item.getCarbsTotalG());
                map.put("fiber_g", item.getFiberG());
                map.put("sugar_g", item.getSugarG());
                map.put("sodium_mg", item.getSodiumMg());
                map.put("potassium_mg", item.getPotassiumMg());
                map.put("calcium_mg", item.getCalciumMg());
                map.put("iron_mg", item.getIronMg());
                map.put("cholesterol_mg", item.getCholesterolMg());
                map.put("serving_size_g", item.getServingSize());
                map.put("source", item.getSource());
                map.put("dietary_tags", item.getDietaryTags());
                map.put("cost_per_serving", item.getCostPerServing());
                results.add(map);
            }
            return results;
        }

        // 2. Check cache
        if (apiCache.containsKey(cleanQuery)) {
            return apiCache.get(cleanQuery);
        }

        // 3. Fallback to external CalorieNinjas API if configured
        if (ninjaApiKey != null && !ninjaApiKey.isBlank()) {
            try {
                String url = "https://api.calorieninjas.com/v1/nutrition?query=" + cleanQuery;
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-Api-Key", ninjaApiKey);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Object rawItems = response.getBody().get("items");
                    if (rawItems instanceof List<?> rawList) {
                        List<Map<String, Object>> formattedList = new ArrayList<>();
                        for (Object o : rawList) {
                            if (o instanceof Map<?, ?> m) {
                                Map<String, Object> itemMap = new HashMap<>();
                                for (Map.Entry<?, ?> e : m.entrySet()) {
                                    itemMap.put(String.valueOf(e.getKey()), e.getValue());
                                }
                                itemMap.put("source", "CALORIE_NINJAS");
                                formattedList.add(itemMap);
                            }
                        }
                        if (!formattedList.isEmpty()) {
                            apiCache.put(cleanQuery, formattedList);
                            return formattedList;
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("External CalorieNinjas API lookup failed for query '{}': {}", cleanQuery, e.getMessage());
            }
        }

        return Collections.emptyList();
    }

    /**
     * Performs multi-ingredient meal analysis with deterministic calculations,
     * portion scaling, balance scoring, and personalized target comparisons.
     */
    public MealAnalysisResult analyzeMeal(MealAnalysisRequest request, UserProfile profile) {
        MealAnalysisResult result = new MealAnalysisResult();
        result.setMealName(request.getMealName() != null ? request.getMealName() : "Custom Meal");
        result.setMealType(request.getMealType() != null ? request.getMealType() : "LUNCH");

        double totalCal = 0.0, totalProtein = 0.0, totalFat = 0.0, totalCarbs = 0.0;
        double totalFiber = 0.0, totalSugar = 0.0, totalSodium = 0.0, totalPotassium = 0.0;
        double totalCalcium = 0.0, totalIron = 0.0, totalCost = 0.0;

        List<Map<String, Object>> itemBreakdown = new ArrayList<>();

        for (FoodAnalysisItemDto itemDto : request.getItems()) {
            String name = itemDto.getFoodName();
            double portion = itemDto.getPortion() != null ? Math.max(0.1, itemDto.getPortion()) : 1.0;
            String unit = itemDto.getUnit() != null ? itemDto.getUnit() : "serving";

            // Multiplier relative to standard 1 serving / 100g
            double multiplier = calculateUnitMultiplier(unit, portion);

            // Lookup food in local DB first
            Optional<FoodItem> foodOpt = foodItemRepository.findFirstByNameIgnoreCase(name);
            if (foodOpt.isEmpty()) {
                List<FoodItem> matches = foodItemRepository.findByNameContainingIgnoreCase(name);
                if (!matches.isEmpty()) {
                    foodOpt = Optional.of(matches.get(0));
                }
            }

            double cal = 0, prot = 0, fat = 0, carbs = 0, fiber = 0, sugar = 0;
            double sodium = 0, potassium = 0, calcium = 0, iron = 0, cost = 0;
            String source = "UNKNOWN";

            if (foodOpt.isPresent()) {
                FoodItem f = foodOpt.get();
                cal = f.getCalories() * multiplier;
                prot = f.getProteinG() * multiplier;
                fat = f.getFatTotalG() * multiplier;
                carbs = f.getCarbsTotalG() * multiplier;
                fiber = f.getFiberG() * multiplier;
                sugar = f.getSugarG() * multiplier;
                sodium = f.getSodiumMg() * multiplier;
                potassium = f.getPotassiumMg() * multiplier;
                calcium = f.getCalciumMg() * multiplier;
                iron = f.getIronMg() * multiplier;
                cost = f.getCostPerServing() * portion;
                source = f.getSource();
            } else {
                // Try external API
                List<Map<String, Object>> apiResults = queryFoodNutrition(name);
                if (!apiResults.isEmpty()) {
                    Map<String, Object> first = apiResults.get(0);
                    cal = getDouble(first, "calories") * multiplier;
                    prot = getDouble(first, "protein_g") * multiplier;
                    fat = getDouble(first, "fat_total_g") * multiplier;
                    carbs = getDouble(first, "carbohydrates_total_g") * multiplier;
                    fiber = getDouble(first, "fiber_g") * multiplier;
                    sugar = getDouble(first, "sugar_g") * multiplier;
                    sodium = getDouble(first, "sodium_mg") * multiplier;
                    potassium = getDouble(first, "potassium_mg") * multiplier;
                    calcium = getDouble(first, "calcium_mg") * multiplier;
                    iron = getDouble(first, "iron_mg") * multiplier;
                    cost = 1.50 * portion;
                    source = "CALORIE_NINJAS";
                }
            }

            totalCal += cal;
            totalProtein += prot;
            totalFat += fat;
            totalCarbs += carbs;
            totalFiber += fiber;
            totalSugar += sugar;
            totalSodium += sodium;
            totalPotassium += potassium;
            totalCalcium += calcium;
            totalIron += iron;
            totalCost += cost;

            Map<String, Object> breakdown = new HashMap<>();
            breakdown.put("name", name);
            breakdown.put("portion", portion);
            breakdown.put("unit", unit);
            breakdown.put("calories", Math.round(cal * 10.0) / 10.0);
            breakdown.put("protein_g", Math.round(prot * 10.0) / 10.0);
            breakdown.put("fat_g", Math.round(fat * 10.0) / 10.0);
            breakdown.put("carbs_g", Math.round(carbs * 10.0) / 10.0);
            breakdown.put("fiber_g", Math.round(fiber * 10.0) / 10.0);
            breakdown.put("source", source);
            itemBreakdown.add(breakdown);
        }

        result.setCalories(Math.round(totalCal * 10.0) / 10.0);
        result.setProteinG(Math.round(totalProtein * 10.0) / 10.0);
        result.setFatG(Math.round(totalFat * 10.0) / 10.0);
        result.setCarbsG(Math.round(totalCarbs * 10.0) / 10.0);
        result.setFiberG(Math.round(totalFiber * 10.0) / 10.0);
        result.setSugarG(Math.round(totalSugar * 10.0) / 10.0);
        result.setSodiumMg(Math.round(totalSodium * 10.0) / 10.0);
        result.setPotassiumMg(Math.round(totalPotassium * 10.0) / 10.0);
        result.setCalciumMg(Math.round(totalCalcium * 10.0) / 10.0);
        result.setIronMg(Math.round(totalIron * 10.0) / 10.0);
        result.setEstimatedCost(Math.round(totalCost * 100.0) / 100.0);
        result.setItemBreakdown(itemBreakdown);

        // Evaluate meal balance, AMDR percentages, and actionable insights
        nutritionIntelligenceService.evaluateMealBalanceAndInsights(result, profile);

        return result;
    }

    private double calculateUnitMultiplier(String unit, double portion) {
        if (unit == null) return portion;
        return switch (unit.toLowerCase()) {
            case "g", "grams" -> portion / 100.0;
            case "ml" -> portion / 100.0;
            case "oz", "ounce", "ounces" -> (portion * 28.35) / 100.0;
            case "cup", "cups" -> portion; // standard 1 cup serving equivalent
            case "piece", "pieces", "slice", "slices" -> portion;
            case "tbsp", "tablespoon" -> portion * 0.15;
            default -> portion;
        };
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number n) return n.doubleValue();
        if (val != null) {
            try { return Double.parseDouble(val.toString()); } catch (NumberFormatException ignored) {}
        }
        return 0.0;
    }
}
