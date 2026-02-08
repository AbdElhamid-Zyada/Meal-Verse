package com.example.mealplanner.repository;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import java.util.Date;
import java.util.Map;

public interface MealRepository {
    Map<MealType, Meal> getMealsForDay(Date date);

    void deleteMeal(Date date, MealType type);
    // Add other methods as needed for planning meals
}
