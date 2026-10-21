package com.example.nutrition.repository;

import com.example.nutrition.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<MealPlan> findByUserUsernameOrderByCreatedAtDesc(String username);
    Optional<MealPlan> findFirstByUserUsernameOrderByCreatedAtDesc(String username);
}
