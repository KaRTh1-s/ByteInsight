package com.example.nutrition.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    private Integer age = 28;
    private String sex = "MALE"; // MALE, FEMALE, OTHER
    private Double heightCm = 175.0;
    private Double weightKg = 70.0;
    private String activityLevel = "MODERATELY_ACTIVE"; // SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE
    private String goal = "MAINTAIN_WEIGHT"; // MAINTAIN_WEIGHT, LOSE_WEIGHT, GAIN_WEIGHT, IMPROVE_HEALTH
    private String dietaryPreference = "ALL"; // ALL, VEGETARIAN, VEGAN, PESCATARIAN, KETO, PALEO, MEDITERRANEAN

    @Column(length = 500)
    private String allergies = ""; // e.g. "Peanuts, Dairy, Shellfish"

    @Column(length = 500)
    private String dislikedFoods = "";

    private Double budgetPerDay = 15.0; // default currency units
    private String preferredCuisines = "Global, Mediterranean, Indian";
    private Integer mealsPerDay = 3;

    // Daily Nutrient Targets (Calculated via Mifflin-St Jeor + AMDR, or customized)
    private Double targetCalories = 2200.0;
    private Double targetProteinG = 130.0;
    private Double targetCarbsG = 250.0;
    private Double targetFatG = 70.0;
    private Double targetFiberG = 30.0;
    private Double targetSodiumMg = 2300.0;
    private Double targetPotassiumMg = 3400.0;
    private Double targetCalciumMg = 1000.0;
    private Double targetIronMg = 14.0;
    private Double targetWaterMl = 2500.0;

    private Boolean customTargets = false;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UserProfile() {}

    public UserProfile(User user) {
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getDietaryPreference() { return dietaryPreference; }
    public void setDietaryPreference(String dietaryPreference) { this.dietaryPreference = dietaryPreference; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getDislikedFoods() { return dislikedFoods; }
    public void setDislikedFoods(String dislikedFoods) { this.dislikedFoods = dislikedFoods; }

    public Double getBudgetPerDay() { return budgetPerDay; }
    public void setBudgetPerDay(Double budgetPerDay) { this.budgetPerDay = budgetPerDay; }

    public String getPreferredCuisines() { return preferredCuisines; }
    public void setPreferredCuisines(String preferredCuisines) { this.preferredCuisines = preferredCuisines; }

    public Integer getMealsPerDay() { return mealsPerDay; }
    public void setMealsPerDay(Integer mealsPerDay) { this.mealsPerDay = mealsPerDay; }

    public Double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Double targetCalories) { this.targetCalories = targetCalories; }

    public Double getTargetProteinG() { return targetProteinG; }
    public void setTargetProteinG(Double targetProteinG) { this.targetProteinG = targetProteinG; }

    public Double getTargetCarbsG() { return targetCarbsG; }
    public void setTargetCarbsG(Double targetCarbsG) { this.targetCarbsG = targetCarbsG; }

    public Double getTargetFatG() { return targetFatG; }
    public void setTargetFatG(Double targetFatG) { this.targetFatG = targetFatG; }

    public Double getTargetFiberG() { return targetFiberG; }
    public void setTargetFiberG(Double targetFiberG) { this.targetFiberG = targetFiberG; }

    public Double getTargetSodiumMg() { return targetSodiumMg; }
    public void setTargetSodiumMg(Double targetSodiumMg) { this.targetSodiumMg = targetSodiumMg; }

    public Double getTargetPotassiumMg() { return targetPotassiumMg; }
    public void setTargetPotassiumMg(Double targetPotassiumMg) { this.targetPotassiumMg = targetPotassiumMg; }

    public Double getTargetCalciumMg() { return targetCalciumMg; }
    public void setTargetCalciumMg(Double targetCalciumMg) { this.targetCalciumMg = targetCalciumMg; }

    public Double getTargetIronMg() { return targetIronMg; }
    public void setTargetIronMg(Double targetIronMg) { this.targetIronMg = targetIronMg; }

    public Double getTargetWaterMl() { return targetWaterMl; }
    public void setTargetWaterMl(Double targetWaterMl) { this.targetWaterMl = targetWaterMl; }

    public Boolean getCustomTargets() { return customTargets; }
    public void setCustomTargets(Boolean customTargets) { this.customTargets = customTargets; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
