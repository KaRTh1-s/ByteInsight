package com.example.nutrition.repository;

import com.example.nutrition.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
    List<ShoppingListItem> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ShoppingListItem> findByUserUsernameOrderByCreatedAtDesc(String username);
    void deleteByUserIdAndIsPurchasedTrue(Long userId);
    void deleteByUserUsernameAndIsPurchasedTrue(String username);
}
