package com.example.nutrition.dto;

import java.util.ArrayList;
import java.util.List;

public class MealAnalysisRequest {
    private String mealName;
    private String mealType = "LUNCH";
    private List<FoodAnalysisItemDto> items = new ArrayList<>();

    public MealAnalysisRequest() {}

    public MealAnalysisRequest(String mealName, String mealType, List<FoodAnalysisItemDto> items) {
        this.mealName = mealName;
        this.mealType = mealType;
        this.items = items;
    }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public List<FoodAnalysisItemDto> getItems() { return items; }
    public void setItems(List<FoodAnalysisItemDto> items) { this.items = items; }
}
