package com.example.nutrition.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shopping_list_items")
public class ShoppingListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String itemName;

    private Double quantity = 1.0;
    private String unit = "pack"; // pack, g, kg, bunch, can, carton, bottle
    private Double estimatedCost = 2.50;
    private Boolean isPurchased = false;
    private String category = "Produce"; // Produce, Dairy & Refrigerated, Bakery & Grains, Proteins, Pantry, Spices

    private LocalDateTime createdAt = LocalDateTime.now();

    public ShoppingListItem() {}

    public ShoppingListItem(User user, String itemName, Double quantity, String unit, Double estimatedCost, String category) {
        this.user = user;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.estimatedCost = estimatedCost;
        this.category = category;
        this.isPurchased = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public Boolean getIsPurchased() { return isPurchased; }
    public void setIsPurchased(Boolean isPurchased) { this.isPurchased = isPurchased; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
