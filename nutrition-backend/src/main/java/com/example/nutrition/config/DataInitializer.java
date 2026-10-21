package com.example.nutrition.config;

import com.example.nutrition.entity.FoodItem;
import com.example.nutrition.entity.Recipe;
import com.example.nutrition.repository.FoodItemRepository;
import com.example.nutrition.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final FoodItemRepository foodItemRepository;
    private final RecipeRepository recipeRepository;

    public DataInitializer(FoodItemRepository foodItemRepository, RecipeRepository recipeRepository) {
        this.foodItemRepository = foodItemRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void run(String... args) {
        if (foodItemRepository.count() == 0) {
            seedFoodItems();
        }
        if (recipeRepository.count() == 0) {
            seedRecipes();
        }
    }

    private void seedFoodItems() {
        List<FoodItem> items = Arrays.asList(
            // Fruits
            new FoodItem("Apple", "Fruits", 182.0, "medium apple (182g)", 95.0, 0.5, 0.3, 25.1, 4.4, 18.9, 1.8, 195.0, 10.9, 0.22, 0.75, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Banana", "Fruits", 118.0, "medium banana (118g)", 105.0, 1.3, 0.4, 27.0, 3.1, 14.4, 1.2, 422.0, 5.9, 0.31, 0.40, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Blueberries", "Fruits", 148.0, "1 cup (148g)", 84.0, 1.1, 0.5, 21.4, 3.6, 14.7, 1.5, 114.0, 8.9, 0.41, 2.50, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Avocado", "Fruits", 150.0, "medium avocado (150g)", 240.0, 3.0, 22.0, 12.8, 10.0, 1.0, 10.5, 708.0, 18.0, 0.82, 1.80, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Orange", "Fruits", 140.0, "medium orange (140g)", 69.0, 1.3, 0.2, 17.6, 3.1, 12.2, 0.0, 232.0, 60.0, 0.14, 0.85, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Strawberries", "Fruits", 152.0, "1 cup sliced (152g)", 49.0, 1.0, 0.5, 11.7, 3.0, 7.4, 1.5, 233.0, 24.3, 0.62, 2.00, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Mango", "Fruits", 165.0, "1 cup diced (165g)", 99.0, 1.4, 0.6, 24.7, 2.6, 22.5, 1.7, 277.0, 18.2, 0.26, 1.50, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),

            // Vegetables
            new FoodItem("Spinach (Raw)", "Vegetables", 100.0, "100g", 23.0, 2.9, 0.4, 3.6, 2.2, 0.4, 79.0, 558.0, 99.0, 2.71, 0.90, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Broccoli", "Vegetables", 100.0, "100g", 34.0, 2.8, 0.4, 6.6, 2.6, 1.7, 33.0, 316.0, 47.0, 0.73, 1.10, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Sweet Potato", "Vegetables", 130.0, "1 medium baked (130g)", 112.0, 2.0, 0.1, 26.2, 3.9, 5.4, 72.0, 438.0, 39.0, 0.79, 0.80, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Carrots", "Vegetables", 128.0, "1 cup chopped (128g)", 52.0, 1.2, 0.3, 12.3, 3.6, 6.1, 88.0, 410.0, 42.0, 0.38, 0.60, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Bell Pepper (Red)", "Vegetables", 120.0, "1 medium (120g)", 37.0, 1.2, 0.4, 7.2, 2.5, 5.0, 4.8, 253.0, 8.4, 0.52, 1.00, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Kale (Raw)", "Vegetables", 100.0, "100g", 49.0, 4.3, 0.9, 8.8, 3.6, 2.3, 38.0, 447.0, 150.0, 1.47, 1.20, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Cucumber", "Vegetables", 100.0, "100g", 15.0, 0.7, 0.1, 3.6, 0.5, 1.7, 2.0, 147.0, 16.0, 0.28, 0.50, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),

            // Whole Grains
            new FoodItem("Rolled Oats (Dry)", "Grains", 50.0, "1/2 cup dry (50g)", 190.0, 6.5, 3.2, 34.0, 5.0, 0.5, 2.0, 180.0, 26.0, 1.80, 0.45, "VEGETARIAN, VEGAN", "GLUTEN", "VERIFIED_LOCAL"),
            new FoodItem("Brown Rice (Cooked)", "Grains", 195.0, "1 cup cooked (195g)", 218.0, 4.5, 1.6, 45.8, 3.5, 0.7, 1.9, 154.0, 19.5, 0.82, 0.55, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Quinoa (Cooked)", "Grains", 185.0, "1 cup cooked (185g)", 222.0, 8.1, 3.6, 39.4, 5.2, 1.6, 13.0, 318.0, 31.5, 2.76, 1.20, "VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Whole Wheat Bread", "Grains", 45.0, "1 slice (45g)", 110.0, 4.0, 1.5, 20.0, 3.0, 2.0, 170.0, 85.0, 30.0, 1.10, 0.35, "VEGETARIAN, VEGAN", "GLUTEN", "VERIFIED_LOCAL"),
            new FoodItem("White Rice (Cooked)", "Grains", 158.0, "1 cup cooked (158g)", 205.0, 4.2, 0.4, 44.5, 0.6, 0.1, 0.0, 55.0, 15.8, 1.90, 0.40, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Lentil Pasta (Cooked)", "Grains", 100.0, "100g cooked", 160.0, 12.0, 1.0, 28.0, 5.0, 1.0, 10.0, 350.0, 20.0, 2.80, 1.50, "VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN", "NONE", "VERIFIED_LOCAL"),

            // Legumes & Plant Proteins
            new FoodItem("Cooked Lentils (Dal)", "Plant Protein", 198.0, "1 cup cooked (198g)", 230.0, 17.9, 0.8, 39.9, 15.6, 3.6, 4.0, 731.0, 37.6, 6.59, 0.70, "VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Chickpeas (Cooked)", "Plant Protein", 164.0, "1 cup cooked (164g)", 269.0, 14.5, 4.2, 45.0, 12.5, 7.9, 11.5, 477.0, 80.4, 4.74, 0.80, "VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Firm Tofu", "Plant Protein", 100.0, "100g", 83.0, 10.0, 5.3, 1.9, 0.9, 0.5, 7.0, 121.0, 282.0, 2.00, 0.95, "VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN, KETO", "SOY", "VERIFIED_LOCAL"),
            new FoodItem("Paneer (Indian Cottage Cheese)", "Dairy", 100.0, "100g", 265.0, 18.3, 20.8, 1.2, 0.0, 1.0, 22.0, 98.0, 480.0, 0.30, 1.40, "VEGETARIAN, GLUTEN_FREE, HIGH_PROTEIN, KETO", "DAIRY", "VERIFIED_LOCAL"),
            new FoodItem("Edamame", "Plant Protein", 155.0, "1 cup (155g)", 188.0, 18.4, 8.1, 13.8, 8.0, 3.4, 9.3, 676.0, 97.6, 3.52, 1.60, "VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN", "SOY", "VERIFIED_LOCAL"),
            new FoodItem("Black Beans (Cooked)", "Plant Protein", 172.0, "1 cup cooked (172g)", 227.0, 15.2, 0.9, 40.8, 15.0, 0.6, 2.0, 611.0, 46.4, 3.61, 0.75, "VEGETARIAN, VEGAN, GLUTEN_FREE, HIGH_PROTEIN", "NONE", "VERIFIED_LOCAL"),

            // Poultry, Seafood & Meats
            new FoodItem("Chicken Breast (Skinless Grilled)", "Poultry", 100.0, "100g grilled", 165.0, 31.0, 3.6, 0.0, 0.0, 0.0, 74.0, 256.0, 15.0, 1.04, 2.20, "HIGH_PROTEIN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Salmon Fillet (Baked)", "Seafood", 100.0, "100g baked", 206.0, 22.1, 12.3, 0.0, 0.0, 0.0, 59.0, 384.0, 9.0, 0.34, 3.80, "HIGH_PROTEIN, GLUTEN_FREE, KETO", "SHELLFISH", "VERIFIED_LOCAL"),
            new FoodItem("Whole Egg (Large)", "Poultry", 50.0, "1 large egg (50g)", 72.0, 6.3, 4.8, 0.4, 0.0, 0.2, 71.0, 69.0, 28.0, 0.88, 0.30, "VEGETARIAN, GLUTEN_FREE, HIGH_PROTEIN, KETO", "EGGS", "VERIFIED_LOCAL"),
            new FoodItem("Egg Whites", "Poultry", 100.0, "100g", 52.0, 10.9, 0.2, 0.7, 0.0, 0.7, 166.0, 163.0, 7.0, 0.08, 0.50, "VEGETARIAN, GLUTEN_FREE, HIGH_PROTEIN, KETO", "EGGS", "VERIFIED_LOCAL"),
            new FoodItem("Canned Tuna (in Water)", "Seafood", 100.0, "100g drained", 116.0, 25.5, 0.8, 0.0, 0.0, 0.0, 338.0, 237.0, 11.0, 1.30, 1.60, "HIGH_PROTEIN, GLUTEN_FREE, KETO", "SHELLFISH", "VERIFIED_LOCAL"),
            new FoodItem("Lean Ground Turkey (93/7)", "Poultry", 100.0, "100g cooked", 170.0, 27.0, 7.0, 0.0, 0.0, 0.0, 85.0, 270.0, 18.0, 1.40, 2.10, "HIGH_PROTEIN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),

            // Dairy & Refrigerated
            new FoodItem("Greek Yogurt (Plain Non-Fat)", "Dairy", 170.0, "1 container (170g)", 100.0, 17.3, 0.7, 6.1, 0.0, 6.1, 61.0, 240.0, 187.0, 0.09, 1.25, "VEGETARIAN, GLUTEN_FREE, HIGH_PROTEIN", "DAIRY", "VERIFIED_LOCAL"),
            new FoodItem("Cottage Cheese (Low Fat 1%)", "Dairy", 100.0, "100g", 72.0, 12.4, 1.0, 2.7, 0.0, 2.7, 406.0, 86.0, 61.0, 0.14, 0.90, "VEGETARIAN, GLUTEN_FREE, HIGH_PROTEIN", "DAIRY", "VERIFIED_LOCAL"),
            new FoodItem("Whole Cow Milk", "Dairy", 244.0, "1 cup (244ml)", 149.0, 7.7, 7.9, 11.7, 0.0, 12.3, 105.0, 322.0, 276.0, 0.07, 0.75, "VEGETARIAN, GLUTEN_FREE", "DAIRY", "VERIFIED_LOCAL"),
            new FoodItem("Unsweetened Almond Milk", "Dairy", 240.0, "1 cup (240ml)", 30.0, 1.0, 2.5, 1.0, 1.0, 0.0, 170.0, 160.0, 450.0, 0.70, 0.65, "VEGETARIAN, VEGAN, GLUTEN_FREE, DAIRY_FREE, KETO", "TREE_NUTS", "VERIFIED_LOCAL"),
            new FoodItem("Cheddar Cheese", "Dairy", 28.0, "1 oz slice (28g)", 113.0, 7.0, 9.3, 0.4, 0.0, 0.1, 183.0, 28.0, 204.0, 0.19, 0.80, "VEGETARIAN, GLUTEN_FREE, KETO", "DAIRY", "VERIFIED_LOCAL"),

            // Nuts, Seeds & Healthy Fats
            new FoodItem("Almonds", "Snacks", 28.0, "1 oz / 23 almonds (28g)", 164.0, 6.0, 14.2, 6.1, 3.5, 1.2, 0.3, 208.0, 76.0, 1.05, 0.90, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "TREE_NUTS", "VERIFIED_LOCAL"),
            new FoodItem("Walnuts", "Snacks", 28.0, "1 oz / 14 halves (28g)", 185.0, 4.3, 18.5, 3.9, 1.9, 0.7, 0.6, 125.0, 28.0, 0.82, 1.10, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "TREE_NUTS", "VERIFIED_LOCAL"),
            new FoodItem("Chia Seeds", "Snacks", 28.0, "1 oz / 2 tbsp (28g)", 138.0, 4.7, 8.7, 12.0, 9.8, 0.0, 4.5, 115.0, 179.0, 2.16, 0.85, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Peanut Butter (Natural)", "Snacks", 32.0, "2 tbsp (32g)", 190.0, 8.0, 16.0, 7.0, 2.0, 2.0, 5.0, 190.0, 17.0, 0.60, 0.50, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "PEANUTS", "VERIFIED_LOCAL"),
            new FoodItem("Extra Virgin Olive Oil", "Pantry", 14.0, "1 tbsp (14g)", 119.0, 0.0, 13.5, 0.0, 0.0, 0.0, 0.3, 0.1, 0.1, 0.08, 0.40, "VEGETARIAN, VEGAN, GLUTEN_FREE, KETO", "NONE", "VERIFIED_LOCAL"),

            // Indian Cuisine & Global Staples
            new FoodItem("Roti / Chapati (Whole Wheat)", "Grains", 40.0, "1 roti (40g)", 104.0, 3.1, 1.5, 20.2, 2.8, 0.4, 90.0, 95.0, 18.0, 1.10, 0.25, "VEGETARIAN, VEGAN", "GLUTEN", "VERIFIED_LOCAL"),
            new FoodItem("Dal Tadka (Cooked)", "Prepared Meals", 200.0, "1 bowl (200g)", 180.0, 9.5, 4.5, 25.0, 6.5, 2.0, 380.0, 390.0, 45.0, 2.80, 1.20, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Palak Paneer", "Prepared Meals", 200.0, "1 bowl (200g)", 240.0, 12.0, 16.0, 9.0, 3.8, 2.5, 420.0, 460.0, 280.0, 2.40, 2.20, "VEGETARIAN, GLUTEN_FREE, KETO", "DAIRY", "VERIFIED_LOCAL"),
            new FoodItem("Chole (Chickpea Masala)", "Prepared Meals", 200.0, "1 bowl (200g)", 210.0, 8.5, 5.0, 33.0, 7.0, 3.0, 450.0, 320.0, 60.0, 2.90, 1.30, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Idli (Steamed Rice & Lentil Cake)", "Grains", 60.0, "1 piece (60g)", 58.0, 2.0, 0.3, 12.0, 1.0, 0.2, 95.0, 35.0, 10.0, 0.50, 0.20, "VEGETARIAN, VEGAN, GLUTEN_FREE", "NONE", "VERIFIED_LOCAL"),
            new FoodItem("Vegetable Khichdi", "Prepared Meals", 250.0, "1 plate (250g)", 260.0, 8.0, 4.5, 46.0, 5.5, 1.8, 380.0, 280.0, 40.0, 2.10, 1.10, "VEGETARIAN, GLUTEN_FREE", "DAIRY", "VERIFIED_LOCAL")
        );

        foodItemRepository.saveAll(items);
    }

    private void seedRecipes() {
        List<Recipe> recipes = Arrays.asList(
            new Recipe(
                "Mediterranean Quinoa & Chickpea Bowl",
                "A vibrant, nutrient-dense bowl packed with plant protein, crisp cucumbers, cherry tomatoes, Kalamata olives, and a light lemon-tahini dressing.",
                "Lunch", 15, 15, 2,
                "[{\"name\":\"Cooked Quinoa\",\"amount\":1,\"unit\":\"cup\"},{\"name\":\"Chickpeas (Cooked)\",\"amount\":1,\"unit\":\"cup\"},{\"name\":\"Cucumber\",\"amount\":100,\"unit\":\"g\"},{\"name\":\"Spinach (Raw)\",\"amount\":50,\"unit\":\"g\"},{\"name\":\"Extra Virgin Olive Oil\",\"amount\":1,\"unit\":\"tbsp\"}]",
                "[\"Rinse and drain chickpeas.\",\"In a serving bowl, fluff cooked quinoa as the base.\",\"Layer chopped cucumber, fresh baby spinach, and chickpeas.\",\"Whisk olive oil, lemon juice, salt, and pepper; drizzle generously.\"]",
                385.0, 15.2, 11.5, 56.0, 11.8, 220.0, 2.80,
                "VEGAN, VEGETARIAN, GLUTEN_FREE, HIGH_FIBER", "NONE", "Mediterranean", null
            ),
            new Recipe(
                "High-Protein Berry Oatmeal with Greek Yogurt",
                "Slow-burning complex carbohydrates paired with antioxidant-rich berries and creamy Greek yogurt for sustained morning energy.",
                "Breakfast", 5, 8, 1,
                "[{\"name\":\"Rolled Oats (Dry)\",\"amount\":50,\"unit\":\"g\"},{\"name\":\"Greek Yogurt (Plain Non-Fat)\",\"amount\":120,\"unit\":\"g\"},{\"name\":\"Blueberries\",\"amount\":0.5,\"unit\":\"cup\"},{\"name\":\"Chia Seeds\",\"amount\":1,\"unit\":\"tbsp\"}]",
                "[\"Simmer rolled oats with water or almond milk until creamy (approx 5 mins).\",\"Remove from heat and stir in Greek yogurt for a velvety texture.\",\"Top with fresh blueberries and chia seeds.\"]",
                370.0, 23.5, 6.8, 54.0, 9.5, 85.0, 2.20,
                "VEGETARIAN, HIGH_PROTEIN, HIGH_FIBER", "DAIRY", "American", null
            ),
            new Recipe(
                "Sheet-Pan Lemon Herb Baked Salmon & Asparagus",
                "Heart-healthy omega-3 rich salmon paired with roasted sweet potato wedges and tender green vegetables.",
                "Dinner", 10, 20, 2,
                "[{\"name\":\"Salmon Fillet (Baked)\",\"amount\":150,\"unit\":\"g\"},{\"name\":\"Sweet Potato\",\"amount\":130,\"unit\":\"g\"},{\"name\":\"Broccoli\",\"amount\":100,\"unit\":\"g\"},{\"name\":\"Extra Virgin Olive Oil\",\"amount\":1,\"unit\":\"tbsp\"}]",
                "[\"Preheat oven to 200°C (400°F).\",\"Toss diced sweet potatoes and broccoli florets with olive oil and spices on a baking sheet.\",\"Place salmon fillets in center with fresh lemon slices and herbs.\",\"Bake for 18-20 minutes until salmon flakes easily.\"]",
                460.0, 36.0, 19.5, 34.0, 7.0, 210.0, 5.20,
                "GLUTEN_FREE, HIGH_PROTEIN, KETO_FRIENDLY", "SHELLFISH", "Global", null
            ),
            new Recipe(
                "Homestyle Palak Paneer with Whole Wheat Roti",
                "Pureed iron-rich spinach tempered with garlic and cumin, gently folded around cubes of protein-rich paneer cheese.",
                "Dinner", 15, 20, 2,
                "[{\"name\":\"Palak Paneer\",\"amount\":200,\"unit\":\"g\"},{\"name\":\"Roti / Chapati (Whole Wheat)\",\"amount\":2,\"unit\":\"pieces\"}]",
                "[\"Blanch fresh spinach leaves for 2 minutes and blend with green chilies.\",\"In a pan, sauté cumin, garlic, and onions until golden.\",\"Pour spinach puree and simmer for 5 minutes.\",\"Add cubed paneer and simmer gently. Serve warm with freshly roasted rotis.\"]",
                448.0, 18.2, 19.0, 49.4, 9.4, 600.0, 2.70,
                "VEGETARIAN, HIGH_FIBER", "DAIRY, GLUTEN", "Indian", null
            ),
            new Recipe(
                "Crispy Tofu & Edamame Quinoa Stir-Fry",
                "A clean 100% plant-based stir-fry featuring golden pan-seared tofu cubes, sweet bell peppers, and edamame over warm fluffy quinoa.",
                "Lunch", 15, 15, 2,
                "[{\"name\":\"Firm Tofu\",\"amount\":150,\"unit\":\"g\"},{\"name\":\"Edamame\",\"amount\":80,\"unit\":\"g\"},{\"name\":\"Bell Pepper (Red)\",\"amount\":100,\"unit\":\"g\"},{\"name\":\"Quinoa (Cooked)\",\"amount\":1,\"unit\":\"cup\"}]",
                "[\"Press tofu to remove moisture, then dice into bite-sized cubes.\",\"Sear in a hot skillet with a touch of sesame oil until golden on all sides.\",\"Toss in sliced bell peppers and shelled edamame for 3-4 minutes.\",\"Season with low-sodium tamari or coconut aminos and serve over cooked quinoa.\"]",
                410.0, 26.5, 14.0, 48.0, 9.8, 290.0, 3.40,
                "VEGAN, VEGETARIAN, GLUTEN_FREE, HIGH_PROTEIN", "SOY", "Asian", null
            ),
            new Recipe(
                "Avocado & Boiled Egg Protein Toast",
                "Toasted whole wheat sourdough topped with creamy mashed Hass avocado, sliced pasture-raised eggs, and red chili flakes.",
                "Breakfast", 5, 8, 1,
                "[{\"name\":\"Whole Wheat Bread\",\"amount\":2,\"unit\":\"slices\"},{\"name\":\"Whole Egg (Large)\",\"amount\":2,\"unit\":\"eggs\"},{\"name\":\"Avocado\",\"amount\":75,\"unit\":\"g\"}]",
                "[\"Hard or soft boil eggs to preference (6-8 minutes).\",\"Toast whole wheat bread slices until golden brown.\",\"Mash ripe avocado with sea salt and black pepper; spread over toast.\",\"Slice eggs and arrange on top. Garnish with microgreens or chili flakes.\"]",
                480.0, 21.0, 23.0, 46.0, 11.0, 480.0, 2.10,
                "VEGETARIAN, HIGH_PROTEIN, HIGH_FIBER", "EGGS, GLUTEN", "Global", null
            )
        );

        recipeRepository.saveAll(recipes);
    }
}
