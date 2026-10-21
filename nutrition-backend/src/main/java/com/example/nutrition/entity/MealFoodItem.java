package com.example.nutrition.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "meal_food_items")
public class MealFoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_entry_id", nullable = false)
    @JsonBackReference
    private MealEntry mealEntry;

    @Column(nullable = false)
    private String foodName;

    private Double portion = 1.0; // multiplier or weight
    private String unit = "serving"; // serving, g, ml, cup, piece, oz, tbsp

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
    private Double cost = 0.0;

    public MealFoodItem() {}

    public MealFoodItem(String foodName, Double portion, String unit,
                        Double calories, Double proteinG, Double fatG, Double carbsG,
                        Double fiberG, Double sugarG, Double sodiumMg,
                        Double potassiumMg, Double calciumMg, Double ironMg, Double cost) {
        this.foodName = foodName;
        this.portion = portion;
        this.unit = unit;
        this.calories = calories;
        this.proteinG = proteinG;
        this.fatG = fatG;
        this.carbsG = carbsG;
        this.fiberG = fiberG;
        this.sugarG = sugarG;
        this.sodiumMg = sodiumMg;
        this.potassiumMg = potassiumMg;
        this.calciumMg = calciumMg;
        this.ironMg = ironMg;
        this.cost = cost;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MealEntry getMealEntry() { return mealEntry; }
    public void setMealEntry(MealEntry mealEntry) { this.mealEntry = mealEntry; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Double getPortion() { return portion; }
    public void setPortion(Double portion) { this.portion = portion; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

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

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}
