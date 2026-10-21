package com.example.nutrition.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealAnalysisResult {
    private String mealName;
    private String mealType;

    // Totals
    private Double calories = 0.0;
    private Double proteinG = 0.0;
    private Double fatG = 0.0;
    private Double carbsG = 0.0;
    private Double fiberG = 0.0;
    private Double sugarG = 0.0;
    private Double sodiumMg = 0.0;
    private Double potassiumMg = 0.0;
    private Double calciumMg = 0.0;
    private Double ironMg = 0.0;
    private Double estimatedCost = 0.0;

    // Macro percentages
    private Double proteinCaloriePct = 0.0;
    private Double fatCaloriePct = 0.0;
    private Double carbsCaloriePct = 0.0;

    // Meal balance score (0-100)
    private Integer balanceScore = 0;
    private String balanceRating = "Moderate"; // Exceptional, Balanced, Moderate, Needs Attention
    private Map<String, Integer> scoreFactors = new HashMap<>();

    // Personalized Target Comparison (% of user's daily reference)
    private Map<String, Double> targetPctMet = new HashMap<>();

    // Categorized Insights
    private List<String> strengths = new ArrayList<>();
    private List<String> potentialGaps = new ArrayList<>();
    private List<InsightMessage> insights = new ArrayList<>();
    private List<FoodSubstitutionDto> substitutions = new ArrayList<>();

    // Breakdown of items analyzed
    private List<Map<String, Object>> itemBreakdown = new ArrayList<>();

    public MealAnalysisResult() {}

    // Getters and Setters
    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProteinG() { return proteinG; }
    public void setProteinG(Double proteinG) { this.proteinG = proteinG; }

    public Double getFatG() { return fatG; }
    public void setFatG(Double fatG) { this.fatG = fatG; }

    public Double getCarbsG() { return carbsG; }
    public void setCarbsG(Double carbsG) { this.carbsG = carbsG; }

    public Double getFiberG() { return fiberG; }
    public void setFiberG(Double fiberG) { this.fiberG = fiberG; }

    public Double getSugarG() { return sugarG; }
    public void setSugarG(Double sugarG) { this.sugarG = sugarG; }

    public Double getSodiumMg() { return sodiumMg; }
    public void setSodiumMg(Double sodiumMg) { this.sodiumMg = sodiumMg; }

    public Double getPotassiumMg() { return potassiumMg; }
    public void setPotassiumMg(Double potassiumMg) { this.potassiumMg = potassiumMg; }

    public Double getCalciumMg() { return calciumMg; }
    public void setCalciumMg(Double calciumMg) { this.calciumMg = calciumMg; }

    public Double getIronMg() { return ironMg; }
    public void setIronMg(Double ironMg) { this.ironMg = ironMg; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public Double getProteinCaloriePct() { return proteinCaloriePct; }
    public void setProteinCaloriePct(Double proteinCaloriePct) { this.proteinCaloriePct = proteinCaloriePct; }

    public Double getFatCaloriePct() { return fatCaloriePct; }
    public void setFatCaloriePct(Double fatCaloriePct) { this.fatCaloriePct = fatCaloriePct; }

    public Double getCarbsCaloriePct() { return carbsCaloriePct; }
    public void setCarbsCaloriePct(Double carbsCaloriePct) { this.carbsCaloriePct = carbsCaloriePct; }

    public Integer getBalanceScore() { return balanceScore; }
    public void setBalanceScore(Integer balanceScore) { this.balanceScore = balanceScore; }

    public String getBalanceRating() { return balanceRating; }
    public void setBalanceRating(String balanceRating) { this.balanceRating = balanceRating; }

    public Map<String, Integer> getScoreFactors() { return scoreFactors; }
    public void setScoreFactors(Map<String, Integer> scoreFactors) { this.scoreFactors = scoreFactors; }

    public Map<String, Double> getTargetPctMet() { return targetPctMet; }
    public void setTargetPctMet(Map<String, Double> targetPctMet) { this.targetPctMet = targetPctMet; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getPotentialGaps() { return potentialGaps; }
    public void setPotentialGaps(List<String> potentialGaps) { this.potentialGaps = potentialGaps; }

    public List<InsightMessage> getInsights() { return insights; }
    public void setInsights(List<InsightMessage> insights) { this.insights = insights; }

    public List<FoodSubstitutionDto> getSubstitutions() { return substitutions; }
    public void setSubstitutions(List<FoodSubstitutionDto> substitutions) { this.substitutions = substitutions; }

    public List<Map<String, Object>> getItemBreakdown() { return itemBreakdown; }
    public void setItemBreakdown(List<Map<String, Object>> itemBreakdown) { this.itemBreakdown = itemBreakdown; }
}
