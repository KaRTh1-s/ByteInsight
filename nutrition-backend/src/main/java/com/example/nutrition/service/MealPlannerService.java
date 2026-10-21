package com.example.nutrition.service;

import com.example.nutrition.entity.*;
import com.example.nutrition.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealPlannerService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;

    public MealPlannerService(MealPlanRepository mealPlanRepository,
                              RecipeRepository recipeRepository,
                              UserRepository userRepository,
                              UserProfileRepository userProfileRepository,
                              ShoppingListItemRepository shoppingListItemRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.shoppingListItemRepository = shoppingListItemRepository;
    }

    @Transactional(readOnly = true)
    public Optional<MealPlan> getCurrentMealPlan(String username) {
        return mealPlanRepository.findFirstByUserUsernameOrderByCreatedAtDesc(username);
    }

    @Transactional
    public MealPlan generatePersonalizedPlan(String username, String planType, Integer durationDays) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        UserProfile profile = userProfileRepository.findByUserUsername(username).orElse(null);

        double dailyCalorieTarget = profile != null && profile.getTargetCalories() != null ? profile.getTargetCalories() : 2000.0;
        String dietaryPref = profile != null && profile.getDietaryPreference() != null ? profile.getDietaryPreference().toUpperCase() : "ALL";
        String userAllergies = profile != null && profile.getAllergies() != null ? profile.getAllergies().toUpperCase() : "";

        // Query available recipes
        List<Recipe> allRecipes = recipeRepository.findAll();
        List<Recipe> safeRecipes = new ArrayList<>();

        for (Recipe r : allRecipes) {
            boolean allergenSafe = true;
            if (!userAllergies.isBlank() && r.getAllergens() != null) {
                for (String allergen : userAllergies.split("[,; ]+")) {
                    if (!allergen.isBlank() && r.getAllergens().toUpperCase().contains(allergen.trim())) {
                        allergenSafe = false;
                        break;
                    }
                }
            }
            boolean dietaryMatch = true;
            if ("VEGETARIAN".equals(dietaryPref) && r.getDietaryTags() != null && !r.getDietaryTags().contains("VEGETARIAN") && !r.getDietaryTags().contains("VEGAN")) {
                dietaryMatch = false;
            }
            if ("VEGAN".equals(dietaryPref) && r.getDietaryTags() != null && !r.getDietaryTags().contains("VEGAN")) {
                dietaryMatch = false;
            }

            if (allergenSafe && dietaryMatch) {
                safeRecipes.add(r);
            }
        }

        if (safeRecipes.isEmpty()) {
            safeRecipes = allRecipes; // fallback to general pool if constraints are too restrictive
        }

        int days = durationDays != null && durationDays > 0 ? durationDays : 7;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days - 1);

        String planName = (dietaryPref.equals("ALL") ? "Balanced" : dietaryPref) + " Personalized " + days + "-Day Plan";
        MealPlan plan = new MealPlan(user, planName, startDate, endDate, dailyCalorieTarget);

        double totalWeeklyCost = 0.0;
        int recipeIndex = 0;

        for (int day = 1; day <= days; day++) {
            // Breakfast
            Recipe bRecipe = pickRecipeByCategory(safeRecipes, "Breakfast", recipeIndex++);
            plan.addItem(new MealPlanItem(day, "BREAKFAST", bRecipe.getTitle(), bRecipe.getDescription(),
                    bRecipe.getCaloriesPerServing(), bRecipe.getProteinG(), bRecipe.getCarbsG(),
                    bRecipe.getFatG(), bRecipe.getFiberG(), bRecipe.getEstimatedCostPerServing(), bRecipe.getId()));
            totalWeeklyCost += bRecipe.getEstimatedCostPerServing();

            // Lunch
            Recipe lRecipe = pickRecipeByCategory(safeRecipes, "Lunch", recipeIndex++);
            plan.addItem(new MealPlanItem(day, "LUNCH", lRecipe.getTitle(), lRecipe.getDescription(),
                    lRecipe.getCaloriesPerServing(), lRecipe.getProteinG(), lRecipe.getCarbsG(),
                    lRecipe.getFatG(), lRecipe.getFiberG(), lRecipe.getEstimatedCostPerServing(), lRecipe.getId()));
            totalWeeklyCost += lRecipe.getEstimatedCostPerServing();

            // Dinner
            Recipe dRecipe = pickRecipeByCategory(safeRecipes, "Dinner", recipeIndex++);
            plan.addItem(new MealPlanItem(day, "DINNER", dRecipe.getTitle(), dRecipe.getDescription(),
                    dRecipe.getCaloriesPerServing(), dRecipe.getProteinG(), dRecipe.getCarbsG(),
                    dRecipe.getFatG(), dRecipe.getFiberG(), dRecipe.getEstimatedCostPerServing(), dRecipe.getId()));
            totalWeeklyCost += dRecipe.getEstimatedCostPerServing();

            // Snack
            plan.addItem(new MealPlanItem(day, "SNACK", "Almonds & Fresh Apple", "Handful of raw almonds with a crisp apple.",
                    220.0, 5.0, 26.0, 12.0, 5.0, 1.25, null));
            totalWeeklyCost += 1.25;
        }

        plan.setEstimatedWeeklyCost(Math.round(totalWeeklyCost * 100.0) / 100.0);
        return mealPlanRepository.save(plan);
    }

    private Recipe pickRecipeByCategory(List<Recipe> recipes, String category, int index) {
        List<Recipe> matching = recipes.stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category))
                .toList();

        if (!matching.isEmpty()) {
            return matching.get(index % matching.size());
        }
        return recipes.get(index % recipes.size());
    }

    @Transactional
    public List<ShoppingListItem> convertPlanToShoppingList(String username, Long mealPlanId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        MealPlan plan = mealPlanRepository.findById(mealPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + mealPlanId));

        List<ShoppingListItem> created = new ArrayList<>();

        // Group ingredients from planned items
        Set<String> added = new HashSet<>();
        for (MealPlanItem item : plan.getItems()) {
            if (item.getRecipeId() != null) {
                recipeRepository.findById(item.getRecipeId()).ifPresent(recipe -> {
                    String title = recipe.getTitle();
                    if (!added.contains(title)) {
                        added.add(title);
                        created.add(new ShoppingListItem(user, title + " ingredients", 1.0, "meal-kit", recipe.getEstimatedCostPerServing(), "Produce & Pantry"));
                    }
                });
            }
        }

        if (created.isEmpty()) {
            created.add(new ShoppingListItem(user, "Rolled Oats", 1.0, "pack", 3.50, "Bakery & Grains"));
            created.add(new ShoppingListItem(user, "Greek Yogurt", 2.0, "tubs", 5.00, "Dairy & Refrigerated"));
            created.add(new ShoppingListItem(user, "Fresh Spinach", 1.0, "bunch", 2.00, "Produce"));
            created.add(new ShoppingListItem(user, "Quinoa", 1.0, "pack", 4.50, "Bakery & Grains"));
            created.add(new ShoppingListItem(user, "Firm Tofu / Chicken Breast", 2.0, "packs", 6.50, "Proteins"));
        }

        return shoppingListItemRepository.saveAll(created);
    }
}
