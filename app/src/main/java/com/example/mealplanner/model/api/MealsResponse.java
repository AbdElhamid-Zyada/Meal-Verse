package com.example.mealplanner.model.api;

import com.example.mealplanner.model.Meal;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealsResponse {
    @SerializedName("meals")
    private List<MealApiModel> meals;

    public List<MealApiModel> getMeals() {
        return meals;
    }

    public void setMeals(List<MealApiModel> meals) {
        this.meals = meals;
    }
}
