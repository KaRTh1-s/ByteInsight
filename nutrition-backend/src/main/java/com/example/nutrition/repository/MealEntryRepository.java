package com.example.nutrition.repository;

import com.example.nutrition.entity.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    List<MealEntry> findByUserIdAndMealDateOrderByCreatedAtAsc(Long userId, LocalDate mealDate);

    List<MealEntry> findByUserUsernameAndMealDateOrderByCreatedAtAsc(String username, LocalDate mealDate);

    Optional<MealEntry> findByUserIdAndMealDateAndMealType(Long userId, LocalDate mealDate, String mealType);

    Optional<MealEntry> findByUserUsernameAndMealDateAndMealType(String username, LocalDate mealDate, String mealType);

    @Query("SELECT m FROM MealEntry m WHERE m.user.id = :userId AND m.mealDate BETWEEN :startDate AND :endDate ORDER BY m.mealDate ASC, m.createdAt ASC")
    List<MealEntry> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT m FROM MealEntry m WHERE m.user.username = :username AND m.mealDate BETWEEN :startDate AND :endDate ORDER BY m.mealDate ASC, m.createdAt ASC")
    List<MealEntry> findByUsernameAndDateRange(@Param("username") String username, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
