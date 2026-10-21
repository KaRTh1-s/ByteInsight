package com.example.nutrition.service;

import com.example.nutrition.dto.MealAnalysisResult;
import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.repository.FoodItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NutritionIntelligenceServiceTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    private NutritionIntelligenceService nutritionIntelligenceService;

    @BeforeEach
    void setUp() {
        nutritionIntelligenceService = new NutritionIntelligenceService(foodItemRepository);
    }

    @Test
    @DisplayName("Should correctly calculate Mifflin-St Jeor BMR, TDEE, and targets for male losing weight")
    void testCalculateTargetsMaleWeightLoss() {
        UserProfile profile = new UserProfile();
        profile.setAge(30);
        profile.setSex("MALE");
        profile.setWeightKg(80.0);
        profile.setHeightCm(180.0);
        profile.setActivityLevel("MODERATELY_ACTIVE"); // 1.55 multiplier
        profile.setGoal("LOSE_WEIGHT"); // -450 kcal deficit
        profile.setCustomTargets(false);

        // BMR = 10*80 + 6.25*180 - 5*30 + 5 = 800 + 1125 - 150 + 5 = 1780
        // TDEE = 1780 * 1.55 = 2759
        // Target = 2759 - 450 = 2309
        nutritionIntelligenceService.calculatePersonalizedTargets(profile);

        assertNotNull(profile.getTargetCalories());
        assertTrue(profile.getTargetCalories() > 2200 && profile.getTargetCalories() < 2400,
                "Expected target calories around ~2309 kcal, got: " + profile.getTargetCalories());
        assertTrue(profile.getTargetProteinG() >= 140.0, "Protein should be high for weight loss goal");
        assertTrue(profile.getTargetFiberG() >= 28.0, "Fiber target should be scaled to caloric intake");
        assertEquals(2300.0, profile.getTargetSodiumMg(), "Sodium reference should match 2300mg guideline");
    }

    @Test
    @DisplayName("Should correctly calculate Mifflin-St Jeor BMR and floor safe calories for female")
    void testCalculateTargetsFemale() {
        UserProfile profile = new UserProfile();
        profile.setAge(25);
        profile.setSex("FEMALE");
        profile.setWeightKg(55.0);
        profile.setHeightCm(160.0);
        profile.setActivityLevel("SEDENTARY"); // 1.20 multiplier
        profile.setGoal("LOSE_WEIGHT");
        profile.setCustomTargets(false);

        nutritionIntelligenceService.calculatePersonalizedTargets(profile);

        assertNotNull(profile.getTargetCalories());
        assertTrue(profile.getTargetCalories() >= 1200.0, "Target calories must not drop below safe 1200 kcal floor");
    }

    @Test
    @DisplayName("Should evaluate meal balance score and produce explainable insights")
    void testEvaluateMealBalanceAndInsights() {
        UserProfile profile = new UserProfile();
        profile.setTargetCalories(2000.0);
        profile.setTargetProteinG(120.0);
        profile.setTargetFiberG(28.0);
        profile.setTargetSodiumMg(2300.0);

        MealAnalysisResult result = new MealAnalysisResult();
        result.setCalories(500.0);
        result.setProteinG(32.0); // High protein (25% of calories)
        result.setFatG(14.0);
        result.setCarbsG(55.0);
        result.setFiberG(8.0); // High fiber
        result.setSodiumMg(380.0); // Safe sodium
        result.setPotassiumMg(650.0);
        result.setCalciumMg(150.0);
        result.setIronMg(3.5);

        nutritionIntelligenceService.evaluateMealBalanceAndInsights(result, profile);

        assertTrue(result.getBalanceScore() >= 75, "Well balanced meal should score at least 75, got: " + result.getBalanceScore());
        assertTrue(result.getBalanceRating().equals("Well Balanced") || result.getBalanceRating().equals("Exceptional Balance"));
        assertFalse(result.getStrengths().isEmpty(), "Should identify strengths");
        assertFalse(result.getInsights().isEmpty(), "Should produce actionable insights");
        assertNotNull(result.getTargetPctMet());
        assertTrue(result.getTargetPctMet().get("protein") > 20.0, "Should compute % of daily protein met");
    }

    @Test
    @DisplayName("Should detect high sodium and low fiber gaps and generate substitutions")
    void testDetectGapsAndSubstitutions() {
        UserProfile profile = new UserProfile();
        profile.setTargetCalories(2000.0);

        MealAnalysisResult result = new MealAnalysisResult();
        result.setCalories(600.0);
        result.setProteinG(10.0); // Low protein
        result.setFatG(25.0);
        result.setCarbsG(80.0);
        result.setFiberG(1.5); // Low fiber gap
        result.setSodiumMg(1100.0); // High sodium gap

        nutritionIntelligenceService.evaluateMealBalanceAndInsights(result, profile);

        assertTrue(result.getBalanceScore() < 60, "Unbalanced meal should have lower score, got: " + result.getBalanceScore());
        assertFalse(result.getPotentialGaps().isEmpty(), "Should identify nutrient gaps");
        assertFalse(result.getSubstitutions().isEmpty(), "Should offer smart substitutions");
    }
}
