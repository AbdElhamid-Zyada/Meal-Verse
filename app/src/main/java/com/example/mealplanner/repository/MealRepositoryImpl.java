package com.example.mealplanner.repository;

import com.example.mealplanner.R;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MealRepositoryImpl implements MealRepository {

    private static MealRepositoryImpl instance;
    // Mock database: Map<DateString, Map<MealType, Meal>>
    private Map<String, Map<MealType, Meal>> plannedMeals;

    private MealRepositoryImpl() {
        plannedMeals = new HashMap<>();
        // Seed some data
        seedData();
    }

    public static MealRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new MealRepositoryImpl();
        }
        return instance;
    }

    private void seedData() {
        // Simplified: Just putting meals for "today" effectively
        // In a real app, we'd use proper date keys.
        // For MVP demo, let's assume we return the same meals for every day unless
        // modified
        // This is a simplification.
    }

    @Override
    public Map<MealType, Meal> getMealsForDay(Date date) {
        String dateKey = getDateKey(date);

        if (!plannedMeals.containsKey(dateKey)) {
            // Seed data for this day if not exists (for demo purposes)
            Map<MealType, Meal> dayMeals = new HashMap<>();
            dayMeals.put(MealType.BREAKFAST,
                    new Meal("1", "Pancakes", R.drawable.ic_launcher_background, 350, 15, MealType.BREAKFAST));
            dayMeals.put(MealType.LUNCH,
                    new Meal("2", "Chicken Teriyaki", R.drawable.ic_launcher_background, 520, 25, MealType.LUNCH));
            dayMeals.put(MealType.DINNER,
                    new Meal("3", "Spaghetti Carbonara", R.drawable.ic_launcher_background, 680, 20, MealType.DINNER));
            plannedMeals.put(dateKey, dayMeals);
        }

        return new HashMap<>(plannedMeals.get(dateKey));
    }

    @Override
    public void deleteMeal(Date date, MealType type) {
        String dateKey = getDateKey(date);
        if (plannedMeals.containsKey(dateKey)) {
            Map<MealType, Meal> dayMeals = plannedMeals.get(dateKey);
            if (dayMeals != null) {
                dayMeals.remove(type);
            }
        }
    }

    private String getDateKey(Date date) {
        // Simple key format: yyyy-MM-dd
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        return sdf.format(date);
    }
}
