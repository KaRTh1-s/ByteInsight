package com.example.nutrition.service;

import com.example.nutrition.entity.MealEntry;
import com.example.nutrition.entity.User;
import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealJournalServiceTest {

    @Mock private MealEntryRepository mealEntryRepository;
    @Mock private MealFoodItemRepository mealFoodItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private FoodItemRepository foodItemRepository;
    @Mock private NutritionIntelligenceService nutritionIntelligenceService;

    private MealJournalService mealJournalService;

    @BeforeEach
    void setUp() {
        mealJournalService = new MealJournalService(
                mealEntryRepository, mealFoodItemRepository, userRepository,
                userProfileRepository, foodItemRepository, nutritionIntelligenceService);
    }

    @Test
    @DisplayName("Should add food item to meal and persist")
    void testAddFoodToMeal() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        MealEntry entry = new MealEntry(user, "LUNCH", LocalDate.now(), "");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(mealEntryRepository.findByUserUsernameAndMealDateAndMealType(eq("testuser"), any(), eq("LUNCH")))
                .thenReturn(Optional.of(entry));
        when(mealEntryRepository.save(any(MealEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        MealEntry result = mealJournalService.addFoodToMeal(
                "testuser", LocalDate.now(), "LUNCH", "Grilled Chicken Breast",
                1.5, "serving", 240.0, 45.0, 5.0, 0.0, 0.0, 150.0, 300.0, 15.0, 1.2, 3.0);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("Grilled Chicken Breast", result.getItems().get(0).getFoodName());
        assertEquals(240.0, result.getItems().get(0).getCalories());
    }
}
