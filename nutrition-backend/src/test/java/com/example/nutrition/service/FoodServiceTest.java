package com.example.nutrition.service;

import com.example.nutrition.dto.FoodAnalysisItemDto;
import com.example.nutrition.dto.MealAnalysisRequest;
import com.example.nutrition.dto.MealAnalysisResult;
import com.example.nutrition.entity.FoodItem;
import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.repository.FoodItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private NutritionIntelligenceService nutritionIntelligenceService;

    private FoodService foodService;

    @BeforeEach
    void setUp() {
        foodService = new FoodService(foodItemRepository, nutritionIntelligenceService, "mock-key");
    }

    @Test
    @DisplayName("Should accurately calculate multi-ingredient meal totals with unit conversion")
    void testAnalyzeMealWithPortionScaling() {
        FoodItem oats = new FoodItem("Rolled Oats (Dry)", "Grains", 50.0, "50g",
                190.0, 6.5, 3.2, 34.0, 5.0, 0.5, 2.0, 180.0, 26.0, 1.80, 0.45, "VEGETARIAN", "NONE", "VERIFIED_LOCAL");

        FoodItem banana = new FoodItem("Banana", "Fruits", 118.0, "medium banana",
                105.0, 1.3, 0.4, 27.0, 3.1, 14.4, 1.2, 422.0, 5.9, 0.31, 0.40, "VEGETARIAN", "NONE", "VERIFIED_LOCAL");

        when(foodItemRepository.findFirstByNameIgnoreCase("Rolled Oats (Dry)")).thenReturn(Optional.of(oats));
        when(foodItemRepository.findFirstByNameIgnoreCase("Banana")).thenReturn(Optional.of(banana));

        MealAnalysisRequest request = new MealAnalysisRequest();
        request.setMealName("Morning Power Bowl");
        request.setMealType("BREAKFAST");
        request.setItems(List.of(
                new FoodAnalysisItemDto("Rolled Oats (Dry)", 2.0, "serving"), // 190 * 2 = 380 kcal
                new FoodAnalysisItemDto("Banana", 1.0, "piece") // 105 * 1 = 105 kcal
        ));

        UserProfile profile = new UserProfile();
        profile.setTargetCalories(2000.0);

        MealAnalysisResult result = foodService.analyzeMeal(request, profile);

        assertNotNull(result);
        assertEquals(485.0, result.getCalories(), 0.5, "Total calories should be 380 + 105 = 485");
        assertEquals(14.3, result.getProteinG(), 0.5, "Total protein should be 13.0 + 1.3 = 14.3");
        assertEquals(2, result.getItemBreakdown().size());
        verify(nutritionIntelligenceService, times(1)).evaluateMealBalanceAndInsights(any(), any());
    }
}
