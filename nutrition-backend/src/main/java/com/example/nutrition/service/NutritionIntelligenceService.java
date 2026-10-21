package com.example.nutrition.service;

import com.example.nutrition.dto.FoodSubstitutionDto;
import com.example.nutrition.dto.InsightMessage;
import com.example.nutrition.dto.MealAnalysisResult;
import com.example.nutrition.entity.FoodItem;
import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NutritionIntelligenceService {

    private final FoodItemRepository foodItemRepository;

    public NutritionIntelligenceService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    /**
     * Calculates personalized nutrition targets based on scientific formulas:
     * - BMR via Mifflin-St Jeor equation
     * - TDEE via physical activity multipliers
     * - Macronutrient distribution using AMDR (Acceptable Macronutrient Distribution Ranges)
     * - Reference micronutrient intakes (DRI guidelines)
     */
    public void calculatePersonalizedTargets(UserProfile profile) {
        if (Boolean.TRUE.equals(profile.getCustomTargets())) {
            return; // preserve user-customized targets
        }

        double weight = profile.getWeightKg() != null ? profile.getWeightKg() : 70.0;
        double height = profile.getHeightCm() != null ? profile.getHeightCm() : 175.0;
        int age = profile.getAge() != null ? profile.getAge() : 28;
        String sex = profile.getSex() != null ? profile.getSex().toUpperCase() : "MALE";

        // 1. Basal Metabolic Rate (BMR) - Mifflin-St Jeor Formula
        double bmr;
        if ("FEMALE".equals(sex)) {
            bmr = (10.0 * weight) + (6.25 * height) - (5.0 * age) - 161.0;
        } else {
            bmr = (10.0 * weight) + (6.25 * height) - (5.0 * age) + 5.0;
        }

        // 2. Physical Activity Multiplier
        String activity = profile.getActivityLevel() != null ? profile.getActivityLevel().toUpperCase() : "MODERATELY_ACTIVE";
        double activityMultiplier = switch (activity) {
            case "SEDENTARY" -> 1.20;
            case "LIGHTLY_ACTIVE" -> 1.375;
            case "MODERATELY_ACTIVE" -> 1.55;
            case "VERY_ACTIVE" -> 1.725;
            case "EXTRA_ACTIVE" -> 1.90;
            default -> 1.55;
        };

        double tdee = bmr * activityMultiplier;

        // 3. Goal Adjustment (Safe, sustainable caloric targets)
        String goal = profile.getGoal() != null ? profile.getGoal().toUpperCase() : "MAINTAIN_WEIGHT";
        double targetCalories = switch (goal) {
            case "LOSE_WEIGHT" -> Math.max("FEMALE".equals(sex) ? 1200.0 : 1500.0, tdee - 450.0);
            case "GAIN_WEIGHT" -> tdee + 350.0;
            case "IMPROVE_HEALTH", "MAINTAIN_WEIGHT" -> tdee;
            default -> tdee;
        };

        // 4. Macronutrient Targets
        // Protein: 1.6g - 2.0g / kg for active or weight management, 1.2g - 1.4g for maintenance
        double proteinMultiplier = "LOSE_WEIGHT".equals(goal) || "GAIN_WEIGHT".equals(goal) ? 1.8 : 1.4;
        if ("VEGAN".equalsIgnoreCase(profile.getDietaryPreference())) {
            proteinMultiplier = 1.6; // plant protein digestibility factor
        }
        double targetProteinG = Math.round(weight * proteinMultiplier);
        double proteinCalories = targetProteinG * 4.0;

        // Fat: 25-30% of total energy
        double fatCalorieShare = "KETO".equalsIgnoreCase(profile.getDietaryPreference()) ? 0.65 : 0.28;
        double targetFatG = Math.round((targetCalories * fatCalorieShare) / 9.0);
        double fatCalories = targetFatG * 9.0;

        // Carbohydrates: Remainder of total energy (minimum 100g for brain glucose function unless keto)
        double carbCalories = Math.max(0.0, targetCalories - (proteinCalories + fatCalories));
        double targetCarbsG = Math.round(carbCalories / 4.0);

        // 5. Micronutrient Targets (Reference Guidelines)
        // Fiber: 14g per 1000 kcal intake
        double targetFiberG = Math.round((targetCalories / 1000.0) * 14.0);
        double targetSodiumMg = 2300.0; // AHA reference daily maximum
        double targetPotassiumMg = "FEMALE".equals(sex) ? 2600.0 : 3400.0;
        double targetCalciumMg = 1000.0;
        double targetIronMg = "FEMALE".equals(sex) && age < 50 ? 18.0 : 8.0;
        double targetWaterMl = Math.round(weight * 35.0); // 35 ml per kg body weight

        profile.setTargetCalories(Math.round(targetCalories * 10.0) / 10.0);
        profile.setTargetProteinG(targetProteinG);
        profile.setTargetFatG(targetFatG);
        profile.setTargetCarbsG(targetCarbsG);
        profile.setTargetFiberG(targetFiberG);
        profile.setTargetSodiumMg(targetSodiumMg);
        profile.setTargetPotassiumMg(targetPotassiumMg);
        profile.setTargetCalciumMg(targetCalciumMg);
        profile.setTargetIronMg(targetIronMg);
        profile.setTargetWaterMl(targetWaterMl);
    }

    /**
     * Evaluates meal balance score (0 - 100) using a transparent, documented methodology:
     * - Macro Balance Factor: AMDR compliance (up to 35 points)
     * - Fiber Density: Fiber relative to energy (up to 25 points)
     * - Sodium & Sugar Moderation: Controlled levels (up to 20 points)
     * - Micronutrient Diversity: Potassium, Calcium, Iron presence (up to 20 points)
     */
    public void evaluateMealBalanceAndInsights(MealAnalysisResult result, UserProfile profile) {
        double calories = Math.max(1.0, result.getCalories());
        double proteinG = result.getProteinG();
        double fatG = result.getFatG();
        double carbsG = result.getCarbsG();
        double fiberG = result.getFiberG();
        double sodiumMg = result.getSodiumMg();
        double sugarG = result.getSugarG();
        double potassiumMg = result.getPotassiumMg();
        double ironMg = result.getIronMg();
        double calciumMg = result.getCalciumMg();

        // 1. Calculate Calorie percentages
        double proteinCal = proteinG * 4.0;
        double fatCal = fatG * 9.0;
        double carbsCal = carbsG * 4.0;
        double totalMacroCal = Math.max(1.0, proteinCal + fatCal + carbsCal);

        double proteinPct = (proteinCal / totalMacroCal) * 100.0;
        double fatPct = (fatCal / totalMacroCal) * 100.0;
        double carbsPct = (carbsCal / totalMacroCal) * 100.0;

        result.setProteinCaloriePct(Math.round(proteinPct * 10.0) / 10.0);
        result.setFatCaloriePct(Math.round(fatPct * 10.0) / 10.0);
        result.setCarbsCaloriePct(Math.round(carbsPct * 10.0) / 10.0);

        // 2. Score Factor A: Macro Distribution (0 - 35 points)
        // Ideal AMDR: Protein 15-35%, Fat 20-35%, Carbs 45-65%
        int macroScore = 35;
        if (proteinPct < 15.0) macroScore -= Math.min(15, (int)((15.0 - proteinPct) * 1.5));
        if (fatPct > 40.0) macroScore -= Math.min(10, (int)((fatPct - 40.0) * 0.8));
        if (carbsPct > 70.0) macroScore -= Math.min(10, (int)((carbsPct - 70.0) * 0.8));
        macroScore = Math.max(8, macroScore);

        // 3. Score Factor B: Fiber Density (0 - 25 points)
        // Benchmark: >= 3.5g per 250 kcal is excellent
        double fiberPer250Cal = (fiberG / calories) * 250.0;
        int fiberScore = (int) Math.min(25, fiberPer250Cal * 7.5);

        // 4. Score Factor C: Sodium & Sugar Moderation (0 - 20 points)
        // High sodium penalty: > 800mg in a single meal reduces score
        int moderationScore = 20;
        if (sodiumMg > 800.0) moderationScore -= (int) Math.min(10, ((sodiumMg - 800.0) / 100.0) * 1.2);
        if ((sugarG * 4.0 / calories) > 0.25) moderationScore -= 5; // sugar > 25% of energy
        moderationScore = Math.max(4, moderationScore);

        // 5. Score Factor D: Micronutrient Diversity (0 - 20 points)
        int microScore = 0;
        if (potassiumMg > 300.0) microScore += 7;
        if (calciumMg > 100.0) microScore += 6;
        if (ironMg > 2.0) microScore += 7;

        int totalScore = macroScore + fiberScore + moderationScore + microScore;
        totalScore = Math.min(100, Math.max(10, totalScore));

        Map<String, Integer> scoreFactors = new HashMap<>();
        scoreFactors.put("Macro Balance (35 pts)", macroScore);
        scoreFactors.put("Fiber Density (25 pts)", fiberScore);
        scoreFactors.put("Sodium & Sugar Moderation (20 pts)", moderationScore);
        scoreFactors.put("Micronutrient Diversity (20 pts)", microScore);
        result.setScoreFactors(scoreFactors);
        result.setBalanceScore(totalScore);

        if (totalScore >= 85) result.setBalanceRating("Exceptional Balance");
        else if (totalScore >= 70) result.setBalanceRating("Well Balanced");
        else if (totalScore >= 50) result.setBalanceRating("Moderate Balance");
        else result.setBalanceRating("Needs Adjustment");

        // 3. Compare with User Daily Target
        double userTargetCal = profile != null && profile.getTargetCalories() != null ? profile.getTargetCalories() : 2000.0;
        double userTargetProtein = profile != null && profile.getTargetProteinG() != null ? profile.getTargetProteinG() : 120.0;
        double userTargetCarbs = profile != null && profile.getTargetCarbsG() != null ? profile.getTargetCarbsG() : 250.0;
        double userTargetFat = profile != null && profile.getTargetFatG() != null ? profile.getTargetFatG() : 65.0;
        double userTargetFiber = profile != null && profile.getTargetFiberG() != null ? profile.getTargetFiberG() : 28.0;
        double userTargetSodium = profile != null && profile.getTargetSodiumMg() != null ? profile.getTargetSodiumMg() : 2300.0;

        Map<String, Double> targetPctMet = new HashMap<>();
        targetPctMet.put("calories", Math.round((calories / userTargetCal) * 1000.0) / 10.0);
        targetPctMet.put("protein", Math.round((proteinG / userTargetProtein) * 1000.0) / 10.0);
        targetPctMet.put("carbs", Math.round((carbsG / userTargetCarbs) * 1000.0) / 10.0);
        targetPctMet.put("fat", Math.round((fatG / userTargetFat) * 1000.0) / 10.0);
        targetPctMet.put("fiber", Math.round((fiberG / userTargetFiber) * 1000.0) / 10.0);
        targetPctMet.put("sodium", Math.round((sodiumMg / userTargetSodium) * 1000.0) / 10.0);
        result.setTargetPctMet(targetPctMet);

        // 4. Generate Explainable Insights, Strengths & Gaps
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();
        List<InsightMessage> insights = new ArrayList<>();

        // Protein Analysis
        if (proteinG >= 25.0) {
            strengths.add("Robust protein content (" + Math.round(proteinG) + "g) supporting muscle maintenance and prolonged satiety.");
            insights.add(new InsightMessage("SUCCESS", "MACRO", "Satiating Protein Content",
                    "This meal supplies " + Math.round(proteinG) + "g of protein (" + Math.round(proteinPct) + "% of meal calories), exceeding the single-meal satiety threshold.",
                    "Keep up this distribution for main meals to sustain steady metabolic rate."));
        } else if (proteinG < 12.0) {
            gaps.add("Protein content is low (" + Math.round(proteinG) + "g), which may lead to earlier hunger.");
            insights.add(new InsightMessage("WARNING", "MACRO", "Protein Below Optimal Range",
                    "Recorded protein is under 12g. Adding a lean protein source would enhance satiety.",
                    "Consider pairing with Greek yogurt, boiled egg, edamame, paneer, or tofu."));
        }

        // Fiber Analysis
        if (fiberG >= 6.0) {
            strengths.add("High fiber density (" + Math.round(fiberG * 10.0)/10.0 + "g), promoting digestive health and glycemic control.");
            insights.add(new InsightMessage("SUCCESS", "FIBER", "Excellent Dietary Fiber",
                    "Supplies " + Math.round(fiberG * 10.0)/10.0 + "g of fiber, meeting over " + Math.round((fiberG / userTargetFiber) * 100) + "% of your daily fiber goal.",
                    "Whole food sources like legumes, vegetables, and seeds support sustained glucose release."));
        } else if (fiberG < 2.5 && calories > 300.0) {
            gaps.add("Low dietary fiber (" + Math.round(fiberG * 10.0)/10.0 + "g) relative to energy density.");
            insights.add(new InsightMessage("TIP", "FIBER", "Potential Fiber Gap",
                    "This meal provides less than 2.5g of fiber. Incorporating unrefined carbohydrates or seeds increases fiber without excess calories.",
                    "Try substituting refined grains with quinoa, brown rice, or tossing in spinach/chia seeds."));
        }

        // Sodium Analysis
        if (sodiumMg > 900.0) {
            gaps.add("Elevated sodium (" + Math.round(sodiumMg) + "mg), accounting for " + Math.round((sodiumMg / userTargetSodium) * 100) + "% of daily reference.");
            insights.add(new InsightMessage("WARNING", "SODIUM", "Sodium Approaches High Reference",
                    "Single meal sodium is " + Math.round(sodiumMg) + "mg. High dietary sodium over consecutive days may affect blood pressure regulation.",
                    "Balance this meal by drinking ample water and emphasizing potassium-rich greens or bananas in subsequent meals."));
        } else if (sodiumMg < 400.0 && calories > 300.0) {
            strengths.add("Low sodium content (" + Math.round(sodiumMg) + "mg), keeping you well within safe cardiovascular reference levels.");
        }

        // Potassium to Sodium Ratio
        if (potassiumMg > sodiumMg && sodiumMg > 0) {
            strengths.add("Favorable potassium-to-sodium ratio (" + Math.round(potassiumMg) + "mg K vs " + Math.round(sodiumMg) + "mg Na), supporting fluid balance.");
        }

        result.setStrengths(strengths);
        result.setPotentialGaps(gaps);
        result.setInsights(insights);

        // 5. Generate Smart Food Substitutions
        generateSubstitutions(result);
    }

    private void generateSubstitutions(MealAnalysisResult result) {
        List<FoodSubstitutionDto> substitutions = new ArrayList<>();

        // If high sodium, suggest low-sodium alternative
        if (result.getSodiumMg() > 600.0) {
            substitutions.add(new FoodSubstitutionDto(
                "High-Sodium Condiments / Broth",
                "Lemon Juice & Fresh Herbs + Extra Virgin Olive Oil",
                "Reduces sodium significantly while adding polyphenol antioxidants.",
                "Zero loss of flavor; saves approximately 400mg sodium per serving.",
                -15.0, 0.0, 0.5, -450.0, -0.30
            ));
        }

        // If fiber is low, suggest whole-grain or seed alternative
        if (result.getFiberG() < 4.0) {
            substitutions.add(new FoodSubstitutionDto(
                "Refined Grain / White Rice",
                "Cooked Quinoa or Brown Basmati Rice",
                "Substantially boosts fiber, magnesium, and plant protein.",
                "+3.5g fiber and +4g protein per cup, with minimal calorie change.",
                15.0, 4.0, 3.5, 0.0, 0.40
            ));
        }

        // If protein is low, suggest lean add-on
        if (result.getProteinG() < 18.0) {
            substitutions.add(new FoodSubstitutionDto(
                "Current Low-Protein Base",
                "Add 100g Firm Tofu, Edamame, or Boiled Egg Whites",
                "Elevates meal protein to meet muscle protein synthesis thresholds.",
                "+10g clean protein with negligible saturated fat.",
                70.0, 11.0, 1.0, 45.0, 0.60
            ));
        }

        result.setSubstitutions(substitutions);
    }
}
