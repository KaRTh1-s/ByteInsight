package com.example.nutrition.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plans")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String planName; // e.g. "7-Day Balanced Plan", "High-Protein Vegetarian Plan"

    private LocalDate startDate;
    private LocalDate endDate;
    private Double targetCaloriesPerDay = 2000.0;
    private Double estimatedWeeklyCost = 75.0;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<MealPlanItem> items = new ArrayList<>();

    public MealPlan() {}

    public MealPlan(User user, String planName, LocalDate startDate, LocalDate endDate, Double targetCaloriesPerDay) {
        this.user = user;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetCaloriesPerDay = targetCaloriesPerDay;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(MealPlanItem item) {
        items.add(item);
        item.setMealPlan(this);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getTargetCaloriesPerDay() { return targetCaloriesPerDay; }
    public void setTargetCaloriesPerDay(Double targetCaloriesPerDay) { this.targetCaloriesPerDay = targetCaloriesPerDay; }

    public Double getEstimatedWeeklyCost() { return estimatedWeeklyCost; }
    public void setEstimatedWeeklyCost(Double estimatedWeeklyCost) { this.estimatedWeeklyCost = estimatedWeeklyCost; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<MealPlanItem> getItems() { return items; }
    public void setItems(List<MealPlanItem> items) { this.items = items; }
}
