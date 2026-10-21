package com.example.nutrition.controller;

import com.example.nutrition.entity.ShoppingListItem;
import com.example.nutrition.entity.User;
import com.example.nutrition.repository.ShoppingListItemRepository;
import com.example.nutrition.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    private final ShoppingListItemRepository shoppingListItemRepository;
    private final UserRepository userRepository;

    public ShoppingListController(ShoppingListItemRepository shoppingListItemRepository, UserRepository userRepository) {
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<ShoppingListItem>> getShoppingList(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(shoppingListItemRepository.findByUserUsernameOrderByCreatedAtDesc(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<?> addShoppingItem(
            Authentication authentication,
            @RequestBody Map<String, Object> body) {
        if (authentication == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String itemName = (String) body.get("itemName");
        if (itemName == null || itemName.isBlank()) {
            return ResponseEntity.badRequest().body("Item name is required");
        }

        Double quantity = body.get("quantity") instanceof Number n ? n.doubleValue() : 1.0;
        String unit = (String) body.getOrDefault("unit", "pack");
        Double cost = body.get("estimatedCost") instanceof Number n ? n.doubleValue() : 2.0;
        String category = (String) body.getOrDefault("category", "Produce");

        ShoppingListItem item = new ShoppingListItem(user, itemName, quantity, unit, cost, category);
        return ResponseEntity.ok(shoppingListItemRepository.save(item));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> togglePurchased(
            Authentication authentication,
            @PathVariable Long id) {
        if (authentication == null) return ResponseEntity.status(401).build();

        return shoppingListItemRepository.findById(id).map(item -> {
            if (!item.getUser().getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(403).body("Not authorized");
            }
            item.setIsPurchased(!Boolean.TRUE.equals(item.getIsPurchased()));
            return ResponseEntity.ok(shoppingListItemRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(Authentication authentication, @PathVariable Long id) {
        if (authentication == null) return ResponseEntity.status(401).build();

        return shoppingListItemRepository.findById(id).map(item -> {
            if (!item.getUser().getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(403).body("Not authorized");
            }
            shoppingListItemRepository.delete(item);
            return ResponseEntity.ok(Map.of("message", "Item deleted"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/clear-purchased")
    public ResponseEntity<?> clearPurchased(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        shoppingListItemRepository.deleteByUserUsernameAndIsPurchasedTrue(authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Purchased items cleared"));
    }
}
