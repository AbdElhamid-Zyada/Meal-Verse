package com.example.mealplanner.model.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IngredientsResponse {
    @SerializedName("meals")
    private List<IngredientApiModel> meals;

    public List<IngredientApiModel> getMeals() {
        return meals;
    }

    public void setMeals(List<IngredientApiModel> meals) {
        this.meals = meals;
    }
}
