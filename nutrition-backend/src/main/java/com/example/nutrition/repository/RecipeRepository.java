package com.example.nutrition.repository;

import com.example.nutrition.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByCategoryIgnoreCase(String category);

    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.dietaryTags) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Recipe> searchRecipes(@Param("query") String query);

    @Query("SELECT r FROM Recipe r WHERE r.caloriesPerServing <= :maxCalories")
    List<Recipe> findByMaxCalories(@Param("maxCalories") Double maxCalories);
}
