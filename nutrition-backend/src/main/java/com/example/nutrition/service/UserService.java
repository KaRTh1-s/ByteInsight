package com.example.nutrition.service;

import com.example.nutrition.entity.User;
import com.example.nutrition.entity.UserProfile;
import com.example.nutrition.repository.UserProfileRepository;
import com.example.nutrition.repository.UserRepository;
import com.example.nutrition.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final UserProfileRepository userProfileRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final NutritionIntelligenceService nutritionIntelligenceService;

    public UserService(UserRepository userRepo,
                       UserProfileRepository userProfileRepo,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       NutritionIntelligenceService nutritionIntelligenceService) {
        this.userRepo = userRepo;
        this.userProfileRepo = userProfileRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.nutritionIntelligenceService = nutritionIntelligenceService;
    }

    @Transactional
    public User registerUser(String username, String email, String password) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepo.save(user);

        // Auto-initialize personalized UserProfile
        UserProfile profile = new UserProfile(savedUser);
        nutritionIntelligenceService.calculatePersonalizedTargets(profile);
        userProfileRepo.save(profile);

        return savedUser;
    }

    public boolean authenticate(String username, String password) {
        return userRepo.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public Map<String, Object> loginAndGenerateToken(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        UserProfile profile = userProfileRepo.findByUserId(user.getId()).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("userId", user.getId());
        response.put("profile", profile);

        return response;
    }

    @Transactional(readOnly = true)
    public UserProfile getUserProfile(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        return userProfileRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile(user);
                    nutritionIntelligenceService.calculatePersonalizedTargets(newProfile);
                    return userProfileRepo.save(newProfile);
                });
    }

    @Transactional
    public UserProfile updateUserProfile(String username, UserProfile updated) {
        UserProfile existing = getUserProfile(username);

        if (updated.getAge() != null) existing.setAge(updated.getAge());
        if (updated.getSex() != null) existing.setSex(updated.getSex());
        if (updated.getHeightCm() != null) existing.setHeightCm(updated.getHeightCm());
        if (updated.getWeightKg() != null) existing.setWeightKg(updated.getWeightKg());
        if (updated.getActivityLevel() != null) existing.setActivityLevel(updated.getActivityLevel());
        if (updated.getGoal() != null) existing.setGoal(updated.getGoal());
        if (updated.getDietaryPreference() != null) existing.setDietaryPreference(updated.getDietaryPreference());
        if (updated.getAllergies() != null) existing.setAllergies(updated.getAllergies());
        if (updated.getDislikedFoods() != null) existing.setDislikedFoods(updated.getDislikedFoods());
        if (updated.getBudgetPerDay() != null) existing.setBudgetPerDay(updated.getBudgetPerDay());
        if (updated.getPreferredCuisines() != null) existing.setPreferredCuisines(updated.getPreferredCuisines());
        if (updated.getMealsPerDay() != null) existing.setMealsPerDay(updated.getMealsPerDay());

        if (Boolean.TRUE.equals(updated.getCustomTargets())) {
            existing.setCustomTargets(true);
            if (updated.getTargetCalories() != null) existing.setTargetCalories(updated.getTargetCalories());
            if (updated.getTargetProteinG() != null) existing.setTargetProteinG(updated.getTargetProteinG());
            if (updated.getTargetCarbsG() != null) existing.setTargetCarbsG(updated.getTargetCarbsG());
            if (updated.getTargetFatG() != null) existing.setTargetFatG(updated.getTargetFatG());
            if (updated.getTargetFiberG() != null) existing.setTargetFiberG(updated.getTargetFiberG());
            if (updated.getTargetSodiumMg() != null) existing.setTargetSodiumMg(updated.getTargetSodiumMg());
        } else {
            existing.setCustomTargets(false);
            nutritionIntelligenceService.calculatePersonalizedTargets(existing);
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return userProfileRepo.save(existing);
    }
}