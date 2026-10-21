package com.example.nutrition.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailySummaryDto {
    private LocalDate date;

    // Actual vs Targets
    private Double totalCalories = 0.0;
    private Double targetCalories = 2200.0;
    private Double remainingCalories = 2200.0;

    private Double totalProteinG = 0.0;
    private Double targetProteinG = 130.0;

    private Double totalCarbsG = 0.0;
    private Double targetCarbsG = 250.0;

    private Double totalFatG = 0.0;
    private Double targetFatG = 70.0;

    private Double totalFiberG = 0.0;
    private Double targetFiberG = 30.0;

    private Double totalSodiumMg = 0.0;
    private Double targetSodiumMg = 2300.0;

    private Double totalPotassiumMg = 0.0;
    private Double targetPotassiumMg = 3400.0;

    private Double totalCalciumMg = 0.0;
    private Double targetCalciumMg = 1000.0;

    private Double totalIronMg = 0.0;
    private Double targetIronMg = 14.0;

    private Double estimatedDailyCost = 0.0;
    private Double dailyBudget = 15.0;

    private Integer dayBalanceScore = 0;
    private String dayBalanceRating = "Needs Data";

    private Integer mealsLoggedCount = 0;
    private Map<String, Object> mealSections = new HashMap<>();

    private List<InsightMessage> insights = new ArrayList<>();
    private List<Map<String, Object>> trendLast7Days = new ArrayList<>();

    public DailySummaryDto() {}

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(Double totalCalories) { this.totalCalories = totalCalories; }

    public Double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Double targetCalories) { this.targetCalories = targetCalories; }

    public Double getRemainingCalories() { return remainingCalories; }
    public void setRemainingCalories(Double remainingCalories) { this.remainingCalories = remainingCalories; }

    public Double getTotalProteinG() { return totalProteinG; }
    public void setTotalProteinG(Double totalProteinG) { this.totalProteinG = totalProteinG; }

    public Double getTargetProteinG() { return targetProteinG; }
    public void setTargetProteinG(Double targetProteinG) { this.targetProteinG = targetProteinG; }

    public Double getTotalCarbsG() { return totalCarbsG; }
    public void setTotalCarbsG(Double totalCarbsG) { this.totalCarbsG = totalCarbsG; }

    public Double getTargetCarbsG() { return targetCarbsG; }
    public void setTargetCarbsG(Double targetCarbsG) { this.targetCarbsG = targetCarbsG; }

    public Double getTotalFatG() { return totalFatG; }
    public void setTotalFatG(Double totalFatG) { this.totalFatG = totalFatG; }

    public Double getTargetFatG() { return targetFatG; }
    public void setTargetFatG(Double targetFatG) { this.targetFatG = targetFatG; }

    public Double getTotalFiberG() { return totalFiberG; }
    public void setTotalFiberG(Double totalFiberG) { this.totalFiberG = totalFiberG; }

    public Double getTargetFiberG() { return targetFiberG; }
    public void setTargetFiberG(Double targetFiberG) { this.targetFiberG = targetFiberG; }

    public Double getTotalSodiumMg() { return totalSodiumMg; }
    public void setTotalSodiumMg(Double totalSodiumMg) { this.totalSodiumMg = totalSodiumMg; }

    public Double getTargetSodiumMg() { return targetSodiumMg; }
    public void setTargetSodiumMg(Double targetSodiumMg) { this.targetSodiumMg = targetSodiumMg; }

    public Double getTotalPotassiumMg() { return totalPotassiumMg; }
    public void setTotalPotassiumMg(Double totalPotassiumMg) { this.totalPotassiumMg = totalPotassiumMg; }

    public Double getTargetPotassiumMg() { return targetPotassiumMg; }
    public void setTargetPotassiumMg(Double targetPotassiumMg) { this.targetPotassiumMg = targetPotassiumMg; }

    public Double getTotalCalciumMg() { return totalCalciumMg; }
    public void setTotalCalciumMg(Double totalCalciumMg) { this.totalCalciumMg = totalCalciumMg; }

    public Double getTargetCalciumMg() { return targetCalciumMg; }
    public void setTargetCalciumMg(Double targetCalciumMg) { this.targetCalciumMg = targetCalciumMg; }

    public Double getTotalIronMg() { return totalIronMg; }
    public void setTotalIronMg(Double totalIronMg) { this.totalIronMg = totalIronMg; }

    public Double getTargetIronMg() { return targetIronMg; }
    public void setTargetIronMg(Double targetIronMg) { this.targetIronMg = targetIronMg; }

    public Double getEstimatedDailyCost() { return estimatedDailyCost; }
    public void setEstimatedDailyCost(Double estimatedDailyCost) { this.estimatedDailyCost = estimatedDailyCost; }

    public Double getDailyBudget() { return dailyBudget; }
    public void setDailyBudget(Double dailyBudget) { this.dailyBudget = dailyBudget; }

    public Integer getDayBalanceScore() { return dayBalanceScore; }
    public void setDayBalanceScore(Integer dayBalanceScore) { this.dayBalanceScore = dayBalanceScore; }

    public String getDayBalanceRating() { return dayBalanceRating; }
    public void setDayBalanceRating(String dayBalanceRating) { this.dayBalanceRating = dayBalanceRating; }

    public Integer getMealsLoggedCount() { return mealsLoggedCount; }
    public void setMealsLoggedCount(Integer mealsLoggedCount) { this.mealsLoggedCount = mealsLoggedCount; }

    public Map<String, Object> getMealSections() { return mealSections; }
    public void setMealSections(Map<String, Object> mealSections) { this.mealSections = mealSections; }

    public List<InsightMessage> getInsights() { return insights; }
    public void setInsights(List<InsightMessage> insights) { this.insights = insights; }

    public List<Map<String, Object>> getTrendLast7Days() { return trendLast7Days; }
    public void setTrendLast7Days(List<Map<String, Object>> trendLast7Days) { this.trendLast7Days = trendLast7Days; }
}
