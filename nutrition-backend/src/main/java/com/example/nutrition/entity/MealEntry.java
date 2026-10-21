package com.example.nutrition.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_entries", indexes = {
    @Index(name = "idx_user_date", columnList = "user_id, mealDate")
})
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    @Column(nullable = false)
    private LocalDate mealDate;

    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "mealEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<MealFoodItem> items = new ArrayList<>();

    public MealEntry() {}

    public MealEntry(User user, String mealType, LocalDate mealDate, String notes) {
        this.user = user;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(MealFoodItem item) {
        items.add(item);
        item.setMealEntry(this);
    }

    public void removeItem(MealFoodItem item) {
        items.remove(item);
        item.setMealEntry(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public LocalDate getMealDate() { return mealDate; }
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<MealFoodItem> getItems() { return items; }
    public void setItems(List<MealFoodItem> items) { this.items = items; }
}
