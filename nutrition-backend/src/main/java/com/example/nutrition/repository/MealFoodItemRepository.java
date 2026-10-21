package com.example.nutrition.repository;

import com.example.nutrition.entity.MealFoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealFoodItemRepository extends JpaRepository<MealFoodItem, Long> {
    List<MealFoodItem> findByMealEntryId(Long mealEntryId);
}
