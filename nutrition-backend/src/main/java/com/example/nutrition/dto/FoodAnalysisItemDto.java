package com.example.nutrition.dto;

public class FoodAnalysisItemDto {
    private String foodName;
    private Double portion = 1.0;
    private String unit = "serving"; // g, serving, cup, piece, oz, tbsp

    public FoodAnalysisItemDto() {}

    public FoodAnalysisItemDto(String foodName, Double portion, String unit) {
        this.foodName = foodName;
        this.portion = portion;
        this.unit = unit;
    }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Double getPortion() { return portion; }
    public void setPortion(Double portion) { this.portion = portion; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
