package com.example.nutrition.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String category; // Breakfast, Lunch, Dinner, Snack, Pre-Workout, Post-Workout
    private Integer prepTimeMinutes = 15;
    private Integer cookTimeMinutes = 20;
    private Integer servings = 2;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String ingredientsJson; // JSON array of ingredient objects {name, amount, unit}

    @Lob
    @Column(columnDefinition = "TEXT")
    private String instructionsJson; // JSON array of step strings

    private Double caloriesPerServing = 400.0;
    private Double proteinG = 25.0;
    private Double fatG = 12.0;
    private Double carbsG = 45.0;
    private Double fiberG = 6.0;
    private Double sodiumMg = 450.0;
    private Double potassiumMg = 600.0;
    private Double calciumMg = 150.0;
    private Double ironMg = 3.5;

    private Double estimatedCostPerServing = 3.50;
    private String dietaryTags = "VEGETARIAN, HIGH_PROTEIN"; // VEGAN, VEGETARIAN, GLUTEN_FREE, KETO, LOW_SODIUM, BUDGET_FRIENDLY
    private String allergens = "NONE";
    private String cuisine = "Mediterranean";
    private String imageUrl;

    public Recipe() {}

    public Recipe(String title, String description, String category,
                  Integer prepTimeMinutes, Integer cookTimeMinutes, Integer servings,
                  String ingredientsJson, String instructionsJson,
                  Double caloriesPerServing, Double proteinG, Double fatG, Double carbsG,
                  Double fiberG, Double sodiumMg, Double estimatedCostPerServing,
                  String dietaryTags, String allergens, String cuisine, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.servings = servings;
        this.ingredientsJson = ingredientsJson;
        this.instructionsJson = instructionsJson;
        this.caloriesPerServing = caloriesPerServing;
        this.proteinG = proteinG;
        this.fatG = fatG;
        this.carbsG = carbsG;
        this.fiberG = fiberG;
        this.sodiumMg = sodiumMg;
        this.estimatedCostPerServing = estimatedCostPerServing;
        this.dietaryTags = dietaryTags;
        this.allergens = allergens;
        this.cuisine = cuisine;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(Integer prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }

    public Integer getCookTimeMinutes() { return cookTimeMinutes; }
    public void setCookTimeMinutes(Integer cookTimeMinutes) { this.cookTimeMinutes = cookTimeMinutes; }

    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }

    public String getIngredientsJson() { return ingredientsJson; }
    public void setIngredientsJson(String ingredientsJson) { this.ingredientsJson = ingredientsJson; }

    public String getInstructionsJson() { return instructionsJson; }
    public void setInstructionsJson(String instructionsJson) { this.instructionsJson = instructionsJson; }

    public Double getCaloriesPerServing() { return caloriesPerServing; }
    public void setCaloriesPerServing(Double caloriesPerServing) { this.caloriesPerServing = caloriesPerServing; }

    public Double getProteinG() { return proteinG; }
    public void setProteinG(Double proteinG) { this.proteinG = proteinG; }

    public Double getFatG() { return fatG; }
    public void setFatG(Double fatG) { this.fatG = fatG; }

    public Double getCarbsG() { return carbsG; }
    public void setCarbsG(Double carbsG) { this.carbsG = carbsG; }

    public Double getFiberG() { return fiberG; }
    public void setFiberG(Double fiberG) { this.fiberG = fiberG; }

    public Double getSodiumMg() { return sodiumMg; }
    public void setSodiumMg(Double sodiumMg) { this.sodiumMg = sodiumMg; }

    public Double getPotassiumMg() { return potassiumMg; }
    public void setPotassiumMg(Double potassiumMg) { this.potassiumMg = potassiumMg; }

    public Double getCalciumMg() { return calciumMg; }
    public void setCalciumMg(Double calciumMg) { this.calciumMg = calciumMg; }

    public Double getIronMg() { return ironMg; }
    public void setIronMg(Double ironMg) { this.ironMg = ironMg; }

    public Double getEstimatedCostPerServing() { return estimatedCostPerServing; }
    public void setEstimatedCostPerServing(Double estimatedCostPerServing) { this.estimatedCostPerServing = estimatedCostPerServing; }

    public String getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(String dietaryTags) { this.dietaryTags = dietaryTags; }

    public String getAllergens() { return allergens; }
    public void setAllergens(String allergens) { this.allergens = allergens; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
