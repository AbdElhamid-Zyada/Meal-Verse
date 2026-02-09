package com.example.mealplanner.model.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AreasResponse {
    @SerializedName("meals")
    private List<AreaApiModel> meals;

    public List<AreaApiModel> getMeals() {
        return meals;
    }

    public void setMeals(List<AreaApiModel> meals) {
        this.meals = meals;
    }
}
