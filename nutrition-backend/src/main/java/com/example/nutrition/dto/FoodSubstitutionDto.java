package com.example.nutrition.dto;

public class FoodSubstitutionDto {
    private String originalFood;
    private String substituteFood;
    private String rationale; // e.g. "Lower sodium & higher fiber"
    private String tradeOff; // e.g. "Slightly less protein (-1.5g), but saves $0.80 per serving"
    private Double calorieDiff;
    private Double proteinDiff;
    private Double fiberDiff;
    private Double sodiumDiff;
    private Double costDiff;

    public FoodSubstitutionDto() {}

    public FoodSubstitutionDto(String originalFood, String substituteFood, String rationale,
                               String tradeOff, Double calorieDiff, Double proteinDiff,
                               Double fiberDiff, Double sodiumDiff, Double costDiff) {
        this.originalFood = originalFood;
        this.substituteFood = substituteFood;
        this.rationale = rationale;
        this.tradeOff = tradeOff;
        this.calorieDiff = calorieDiff;
        this.proteinDiff = proteinDiff;
        this.fiberDiff = fiberDiff;
        this.sodiumDiff = sodiumDiff;
        this.costDiff = costDiff;
    }

    // Getters and Setters
    public String getOriginalFood() { return originalFood; }
    public void setOriginalFood(String originalFood) { this.originalFood = originalFood; }

    public String getSubstituteFood() { return substituteFood; }
    public void setSubstituteFood(String substituteFood) { this.substituteFood = substituteFood; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public String getTradeOff() { return tradeOff; }
    public void setTradeOff(String tradeOff) { this.tradeOff = tradeOff; }

    public Double getCalorieDiff() { return calorieDiff; }
    public void setCalorieDiff(Double calorieDiff) { this.calorieDiff = calorieDiff; }

    public Double getProteinDiff() { return proteinDiff; }
    public void setProteinDiff(Double proteinDiff) { this.proteinDiff = proteinDiff; }

    public Double getFiberDiff() { return fiberDiff; }
    public void setFiberDiff(Double fiberDiff) { this.fiberDiff = fiberDiff; }

    public Double getSodiumDiff() { return sodiumDiff; }
    public void setSodiumDiff(Double sodiumDiff) { this.sodiumDiff = sodiumDiff; }

    public Double getCostDiff() { return costDiff; }
    public void setCostDiff(Double costDiff) { this.costDiff = costDiff; }
}
