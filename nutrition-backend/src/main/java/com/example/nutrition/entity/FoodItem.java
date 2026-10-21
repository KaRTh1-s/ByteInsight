package com.example.nutrition.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "food_items", indexes = {
    @Index(name = "idx_food_name", columnList = "name"),
    @Index(name = "idx_food_category", columnList = "category")
})
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category; // Fruits, Vegetables, Grains, Dairy, Poultry, Seafood, Plant Protein, Snacks, Prepared Meals
    private Double servingSize = 100.0;
    private String servingUnit = "g"; // g, ml, cup, piece, oz, tbsp

    private Double calories = 0.0; // kcal
    private Double proteinG = 0.0;
    private Double fatTotalG = 0.0;
    private Double carbsTotalG = 0.0;
    private Double fiberG = 0.0;
    private Double sugarG = 0.0;
    private Double sodiumMg = 0.0;
    private Double potassiumMg = 0.0;
    private Double calciumMg = 0.0;
    private Double ironMg = 0.0;
    private Double cholesterolMg = 0.0;
    private Double vitaminCMg = 0.0;
    private Double vitaminAIu = 0.0;

    private Double costPerServing = 1.0; // currency units
    private String dietaryTags = "VEGETARIAN"; // VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN, LOW_CARB, DAIRY_FREE
    private String allergens = "NONE"; // PEANUTS, DAIRY, GLUTEN, SHELLFISH, SOY, EGGS, TREE_NUTS, NONE
    private String source = "VERIFIED_LOCAL"; // VERIFIED_LOCAL, CALORIE_NINJAS, USDA, USER_CUSTOM
    private Double confidenceScore = 1.0; // 0.0 - 1.0

    public FoodItem() {}

    public FoodItem(String name, String category, Double servingSize, String servingUnit,
                    Double calories, Double proteinG, Double fatTotalG, Double carbsTotalG,
                    Double fiberG, Double sugarG, Double sodiumMg, Double potassiumMg,
                    Double calciumMg, Double ironMg, Double costPerServing,
                    String dietaryTags, String allergens, String source) {
        this.name = name;
        this.category = category;
        this.servingSize = servingSize;
        this.servingUnit = servingUnit;
        this.calories = calories;
        this.proteinG = proteinG;
        this.fatTotalG = fatTotalG;
        this.carbsTotalG = carbsTotalG;
        this.fiberG = fiberG;
        this.sugarG = sugarG;
        this.sodiumMg = sodiumMg;
        this.potassiumMg = potassiumMg;
        this.calciumMg = calciumMg;
        this.ironMg = ironMg;
        this.costPerServing = costPerServing;
        this.dietaryTags = dietaryTags;
        this.allergens = allergens;
        this.source = source;
        this.confidenceScore = 1.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getServingSize() { return servingSize; }
    public void setServingSize(Double servingSize) { this.servingSize = servingSize; }

    public String getServingUnit() { return servingUnit; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProteinG() { return proteinG; }
    public void setProteinG(Double proteinG) { this.proteinG = proteinG; }

    public Double getFatTotalG() { return fatTotalG; }
    public void setFatTotalG(Double fatTotalG) { this.fatTotalG = fatTotalG; }

    public Double getCarbsTotalG() { return carbsTotalG; }
    public void setCarbsTotalG(Double carbsTotalG) { this.carbsTotalG = carbsTotalG; }

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

    public Double getCholesterolMg() { return cholesterolMg; }
    public void setCholesterolMg(Double cholesterolMg) { this.cholesterolMg = cholesterolMg; }

    public Double getVitaminCMg() { return vitaminCMg; }
    public void setVitaminCMg(Double vitaminCMg) { this.vitaminCMg = vitaminCMg; }

    public Double getVitaminAIu() { return vitaminAIu; }
    public void setVitaminAIu(Double vitaminAIu) { this.vitaminAIu = vitaminAIu; }

    public Double getCostPerServing() { return costPerServing; }
    public void setCostPerServing(Double costPerServing) { this.costPerServing = costPerServing; }

    public String getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(String dietaryTags) { this.dietaryTags = dietaryTags; }

    public String getAllergens() { return allergens; }
    public void setAllergens(String allergens) { this.allergens = allergens; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
}
