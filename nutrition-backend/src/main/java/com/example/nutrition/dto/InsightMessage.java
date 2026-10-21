package com.example.nutrition.dto;

public class InsightMessage {
    private String type; // SUCCESS, WARNING, INFO, TIP
    private String category; // MACRO, FIBER, SODIUM, MICRONUTRIENT, BUDGET, BALANCE
    private String title;
    private String message;
    private String actionableSuggestion;

    public InsightMessage() {}

    public InsightMessage(String type, String category, String title, String message, String actionableSuggestion) {
        this.type = type;
        this.category = category;
        this.title = title;
        this.message = message;
        this.actionableSuggestion = actionableSuggestion;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getActionableSuggestion() { return actionableSuggestion; }
    public void setActionableSuggestion(String actionableSuggestion) { this.actionableSuggestion = actionableSuggestion; }
}
