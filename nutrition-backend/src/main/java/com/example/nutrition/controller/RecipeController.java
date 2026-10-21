package com.example.nutrition.controller;

import com.example.nutrition.entity.Recipe;
import com.example.nutrition.repository.RecipeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category) {
        if (query != null && !query.isBlank()) {
            return ResponseEntity.ok(recipeRepository.searchRecipes(query.trim()));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(recipeRepository.findByCategoryIgnoreCase(category.trim()));
        }
        return ResponseEntity.ok(recipeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long id) {
        return recipeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
