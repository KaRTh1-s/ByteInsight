package com.example.nutrition.repository;

import com.example.nutrition.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByNameContainingIgnoreCase(String name);

    Optional<FoodItem> findFirstByNameIgnoreCase(String name);

    List<FoodItem> findByCategoryIgnoreCase(String category);

    @Query("SELECT f FROM FoodItem f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(f.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FoodItem> searchFoods(@Param("query") String query);

    @Query("SELECT f FROM FoodItem f WHERE f.category = :category AND f.id != :excludeId ORDER BY ABS(f.proteinG - :targetProtein) ASC")
    List<FoodItem> findSubstitutesByProtein(@Param("category") String category, @Param("excludeId") Long excludeId, @Param("targetProtein") Double targetProtein);

    @Query("SELECT f FROM FoodItem f WHERE f.category = :category AND f.id != :excludeId ORDER BY f.costPerServing ASC")
    List<FoodItem> findBudgetSubstitutes(@Param("category") String category, @Param("excludeId") Long excludeId);
}
