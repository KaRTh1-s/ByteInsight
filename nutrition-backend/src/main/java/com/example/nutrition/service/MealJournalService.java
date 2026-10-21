package com.example.nutrition.service;

import com.example.nutrition.dto.DailySummaryDto;
import com.example.nutrition.dto.InsightMessage;
import com.example.nutrition.entity.*;
import com.example.nutrition.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealJournalService {

    private final MealEntryRepository mealEntryRepository;
    private final MealFoodItemRepository mealFoodItemRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FoodItemRepository foodItemRepository;
    private final NutritionIntelligenceService nutritionIntelligenceService;

    public MealJournalService(MealEntryRepository mealEntryRepository,
                              MealFoodItemRepository mealFoodItemRepository,
                              UserRepository userRepository,
                              UserProfileRepository userProfileRepository,
                              FoodItemRepository foodItemRepository,
                              NutritionIntelligenceService nutritionIntelligenceService) {
        this.mealEntryRepository = mealEntryRepository;
        this.mealFoodItemRepository = mealFoodItemRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.foodItemRepository = foodItemRepository;
        this.nutritionIntelligenceService = nutritionIntelligenceService;
    }

    @Transactional(readOnly = true)
    public List<MealEntry> getMealsForDate(String username, LocalDate date) {
        return mealEntryRepository.findByUserUsernameAndMealDateOrderByCreatedAtAsc(username, date);
    }

    @Transactional
    public MealEntry addFoodToMeal(String username, LocalDate date, String mealType,
                                   String foodName, Double portion, String unit,
                                   Double calories, Double protein, Double fat, Double carbs,
                                   Double fiber, Double sodium, Double potassium, Double calcium, Double iron, Double cost) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        String normalizedMealType = mealType != null ? mealType.toUpperCase() : "LUNCH";
        LocalDate normalizedDate = date != null ? date : LocalDate.now();

        MealEntry entry = mealEntryRepository.findByUserUsernameAndMealDateAndMealType(username, normalizedDate, normalizedMealType)
                .orElseGet(() -> mealEntryRepository.save(new MealEntry(user, normalizedMealType, normalizedDate, "")));

        // If nutrient numbers were not provided by frontend, resolve from foodItemRepository
        if (calories == null || calories == 0.0) {
            Optional<FoodItem> foodOpt = foodItemRepository.findFirstByNameIgnoreCase(foodName);
            if (foodOpt.isPresent()) {
                FoodItem f = foodOpt.get();
                double mult = portion != null ? portion : 1.0;
                calories = f.getCalories() * mult;
                protein = f.getProteinG() * mult;
                fat = f.getFatTotalG() * mult;
                carbs = f.getCarbsTotalG() * mult;
                fiber = f.getFiberG() * mult;
                sodium = f.getSodiumMg() * mult;
                potassium = f.getPotassiumMg() * mult;
                calcium = f.getCalciumMg() * mult;
                iron = f.getIronMg() * mult;
                cost = f.getCostPerServing() * mult;
            }
        }

        MealFoodItem item = new MealFoodItem(
                foodName,
                portion != null ? portion : 1.0,
                unit != null ? unit : "serving",
                calories != null ? calories : 0.0,
                protein != null ? protein : 0.0,
                fat != null ? fat : 0.0,
                carbs != null ? carbs : 0.0,
                fiber != null ? fiber : 0.0,
                0.0, // sugar
                sodium != null ? sodium : 0.0,
                potassium != null ? potassium : 0.0,
                calcium != null ? calcium : 0.0,
                iron != null ? iron : 0.0,
                cost != null ? cost : 1.0
        );

        entry.addItem(item);
        return mealEntryRepository.save(entry);
    }

    @Transactional
    public void deleteItem(String username, Long mealId, Long itemId) {
        MealEntry entry = mealEntryRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found with id: " + mealId));

        if (!entry.getUser().getUsername().equals(username)) {
            throw new SecurityException("Unauthorized to modify this meal");
        }

        entry.getItems().removeIf(item -> item.getId().equals(itemId));
        if (entry.getItems().isEmpty()) {
            mealEntryRepository.delete(entry);
        } else {
            mealEntryRepository.save(entry);
        }
    }

    @Transactional
    public void deleteMeal(String username, Long mealId) {
        MealEntry entry = mealEntryRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found with id: " + mealId));

        if (!entry.getUser().getUsername().equals(username)) {
            throw new SecurityException("Unauthorized to modify this meal");
        }

        mealEntryRepository.delete(entry);
    }

    @Transactional
    public List<MealEntry> duplicateDayMeals(String username, LocalDate sourceDate, LocalDate targetDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<MealEntry> sourceMeals = mealEntryRepository.findByUserUsernameAndMealDateOrderByCreatedAtAsc(username, sourceDate);
        List<MealEntry> newMeals = new ArrayList<>();

        for (MealEntry src : sourceMeals) {
            MealEntry copy = new MealEntry(user, src.getMealType(), targetDate, "Duplicated from " + sourceDate);
            for (MealFoodItem item : src.getItems()) {
                copy.addItem(new MealFoodItem(
                        item.getFoodName(), item.getPortion(), item.getUnit(),
                        item.getCalories(), item.getProteinG(), item.getFatG(), item.getCarbsG(),
                        item.getFiberG(), item.getSugarG(), item.getSodiumMg(),
                        item.getPotassiumMg(), item.getCalciumMg(), item.getIronMg(), item.getCost()
                ));
            }
            newMeals.add(mealEntryRepository.save(copy));
        }

        return newMeals;
    }

    @Transactional(readOnly = true)
    public DailySummaryDto getDailySummary(String username, LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        UserProfile profile = userProfileRepository.findByUserUsername(username).orElse(null);

        DailySummaryDto summary = new DailySummaryDto();
        summary.setDate(queryDate);

        if (profile != null) {
            summary.setTargetCalories(profile.getTargetCalories());
            summary.setTargetProteinG(profile.getTargetProteinG());
            summary.setTargetCarbsG(profile.getTargetCarbsG());
            summary.setTargetFatG(profile.getTargetFatG());
            summary.setTargetFiberG(profile.getTargetFiberG());
            summary.setTargetSodiumMg(profile.getTargetSodiumMg());
            summary.setTargetPotassiumMg(profile.getTargetPotassiumMg());
            summary.setTargetCalciumMg(profile.getTargetCalciumMg());
            summary.setTargetIronMg(profile.getTargetIronMg());
            summary.setDailyBudget(profile.getBudgetPerDay());
        }

        List<MealEntry> meals = mealEntryRepository.findByUserUsernameAndMealDateOrderByCreatedAtAsc(username, queryDate);
        double totalCal = 0, totalProt = 0, totalFat = 0, totalCarbs = 0;
        double totalFib = 0, totalSod = 0, totalPot = 0, totalCalc = 0, totalIron = 0, totalCost = 0;

        Map<String, Object> sections = new LinkedHashMap<>();
        sections.put("BREAKFAST", new ArrayList<Map<String, Object>>());
        sections.put("LUNCH", new ArrayList<Map<String, Object>>());
        sections.put("DINNER", new ArrayList<Map<String, Object>>());
        sections.put("SNACK", new ArrayList<Map<String, Object>>());

        int count = 0;
        for (MealEntry meal : meals) {
            List<Map<String, Object>> sectionList = (List<Map<String, Object>>) sections.computeIfAbsent(meal.getMealType(), k -> new ArrayList<>());

            for (MealFoodItem it : meal.getItems()) {
                totalCal += it.getCalories();
                totalProt += it.getProteinG();
                totalFat += it.getFatG();
                totalCarbs += it.getCarbsG();
                totalFib += it.getFiberG();
                totalSod += it.getSodiumMg();
                totalPot += it.getPotassiumMg();
                totalCalc += it.getCalciumMg();
                totalIron += it.getIronMg();
                totalCost += it.getCost();

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("mealId", meal.getId());
                itemMap.put("itemId", it.getId());
                itemMap.put("foodName", it.getFoodName());
                itemMap.put("portion", it.getPortion());
                itemMap.put("unit", it.getUnit());
                itemMap.put("calories", it.getCalories());
                itemMap.put("proteinG", it.getProteinG());
                itemMap.put("fatG", it.getFatG());
                itemMap.put("carbsG", it.getCarbsG());
                itemMap.put("fiberG", it.getFiberG());
                itemMap.put("sodiumMg", it.getSodiumMg());
                itemMap.put("cost", it.getCost());
                sectionList.add(itemMap);
                count++;
            }
        }

        summary.setMealsLoggedCount(count);
        summary.setMealSections(sections);
        summary.setTotalCalories(Math.round(totalCal * 10.0) / 10.0);
        summary.setRemainingCalories(Math.max(0.0, Math.round((summary.getTargetCalories() - totalCal) * 10.0) / 10.0));
        summary.setTotalProteinG(Math.round(totalProt * 10.0) / 10.0);
        summary.setTotalFatG(Math.round(totalFat * 10.0) / 10.0);
        summary.setTotalCarbsG(Math.round(totalCarbs * 10.0) / 10.0);
        summary.setTotalFiberG(Math.round(totalFib * 10.0) / 10.0);
        summary.setTotalSodiumMg(Math.round(totalSod * 10.0) / 10.0);
        summary.setTotalPotassiumMg(Math.round(totalPot * 10.0) / 10.0);
        summary.setTotalCalciumMg(Math.round(totalCalc * 10.0) / 10.0);
        summary.setTotalIronMg(Math.round(totalIron * 10.0) / 10.0);
        summary.setEstimatedDailyCost(Math.round(totalCost * 100.0) / 100.0);

        // Calculate Daily Balance Score if meals are logged
        if (count > 0 && totalCal > 200) {
            double protCal = totalProt * 4.0;
            double fatCal = totalFat * 9.0;
            double carbsCal = totalCarbs * 4.0;
            double macroTotal = Math.max(1.0, protCal + fatCal + carbsCal);

            double protPct = (protCal / macroTotal) * 100.0;
            double fatPct = (fatCal / macroTotal) * 100.0;
            double carbsPct = (carbsCal / macroTotal) * 100.0;

            int score = 40;
            if (protPct >= 15.0 && protPct <= 35.0) score += 20;
            if (fatPct >= 20.0 && fatPct <= 35.0) score += 15;
            if (totalFib >= 20.0) score += 15;
            if (totalSod <= summary.getTargetSodiumMg()) score += 10;
            summary.setDayBalanceScore(Math.min(100, score));
            summary.setDayBalanceRating(score >= 80 ? "Well Balanced" : score >= 60 ? "Moderate" : "Needs Attention");
        } else {
            summary.setDayBalanceScore(0);
            summary.setDayBalanceRating("No Logged Meals");
        }

        // Generate Actionable Daily Insights
        List<InsightMessage> insights = new ArrayList<>();
        if (count == 0) {
            insights.add(new InsightMessage("INFO", "BALANCE", "No Meals Logged Today",
                    "Log breakfast, lunch, or a snack to unlock real-time nutrition intelligence.",
                    "Click 'Quick Add' or use Food Analyzer to start tracking."));
        } else {
            if (totalProt < summary.getTargetProteinG() * 0.5) {
                insights.add(new InsightMessage("WARNING", "MACRO", "Protein Intake Behind Target",
                        "You've recorded " + Math.round(totalProt) + "g of protein (" + Math.round((totalProt/summary.getTargetProteinG())*100) + "% of daily target).",
                        "Include protein-rich foods like Greek yogurt, eggs, lentils, or tofu in your next meal."));
            } else {
                insights.add(new InsightMessage("SUCCESS", "MACRO", "Protein Target On Track",
                        "You have achieved " + Math.round((totalProt/summary.getTargetProteinG())*100) + "% of your recommended protein goal today.",
                        "Great consistency supporting muscle repair and sustained satiety."));
            }

            if (totalFib < summary.getTargetFiberG() * 0.5) {
                insights.add(new InsightMessage("TIP", "FIBER", "Boost Fiber Density",
                        "Fiber intake is currently " + Math.round(totalFib) + "g vs reference target of " + Math.round(summary.getTargetFiberG()) + "g.",
                        "Adding a cup of berries, chia seeds, or raw vegetables can easily close this gap."));
            }

            if (totalSod > summary.getTargetSodiumMg() * 0.9) {
                insights.add(new InsightMessage("WARNING", "SODIUM", "Approaching Sodium Reference Limit",
                        "Logged sodium is at " + Math.round(totalSod) + "mg, nearing your daily guideline of " + Math.round(summary.getTargetSodiumMg()) + "mg.",
                        "Hydrate well and opt for potassium-rich fresh fruits or steamed vegetables for remaining meals."));
            }
        }
        summary.setInsights(insights);

        // Compute 7-day intake trend points
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate startRange = queryDate.minusDays(6);
        List<MealEntry> weekMeals = mealEntryRepository.findByUsernameAndDateRange(username, startRange, queryDate);

        Map<LocalDate, Double> caloriesByDate = new HashMap<>();
        Map<LocalDate, Double> proteinByDate = new HashMap<>();
        Map<LocalDate, Double> carbsByDate = new HashMap<>();
        Map<LocalDate, Double> fatByDate = new HashMap<>();

        for (MealEntry m : weekMeals) {
            LocalDate d = m.getMealDate();
            for (MealFoodItem it : m.getItems()) {
                caloriesByDate.put(d, caloriesByDate.getOrDefault(d, 0.0) + it.getCalories());
                proteinByDate.put(d, proteinByDate.getOrDefault(d, 0.0) + it.getProteinG());
                carbsByDate.put(d, carbsByDate.getOrDefault(d, 0.0) + it.getCarbsG());
                fatByDate.put(d, fatByDate.getOrDefault(d, 0.0) + it.getFatG());
            }
        }

        for (int i = 6; i >= 0; i--) {
            LocalDate day = queryDate.minusDays(i);
            Map<String, Object> point = new HashMap<>();
            point.put("date", day.toString());
            point.put("dayName", day.getDayOfWeek().name().substring(0, 3));
            point.put("calories", Math.round(caloriesByDate.getOrDefault(day, 0.0)));
            point.put("protein", Math.round(proteinByDate.getOrDefault(day, 0.0)));
            point.put("carbs", Math.round(carbsByDate.getOrDefault(day, 0.0)));
            point.put("fat", Math.round(fatByDate.getOrDefault(day, 0.0)));
            point.put("targetCalories", Math.round(summary.getTargetCalories()));
            trend.add(point);
        }
        summary.setTrendLast7Days(trend);

        return summary;
    }
}
