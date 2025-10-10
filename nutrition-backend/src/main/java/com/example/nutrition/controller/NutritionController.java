package com.example.nutrition.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class NutritionController {

    private final String apikey = "zVM3ISsI8JliAEecW5pZEQ==rbtbIItWwTWf8kIN";

    @PostMapping("/nutrition")
    public ResponseEntity<Object> getNutrition(@RequestBody Map<String, String> body) {
        String query = body.get("query");

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query cannot be empty");
        }

        String url = "https://api.calorieninjas.com/v1/nutrition?query=" + query;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apikey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            Object items = response.getBody().get("items");
            return ResponseEntity.ok(items);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch data: " + e.getMessage());
        }
    }
}
