package com.example.mealplanner.model;

import java.io.Serializable;

public class Meal implements Serializable {
    private String id;
    private String name;
    private int imageResId; // Using resource ID for mock data, normally would be URL
    private int calories;
    private int preparationTimeMinutes;
    private MealType type;

    public Meal(String id, String name, int imageResId, int calories, int preparationTimeMinutes, MealType type) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.calories = calories;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getCalories() {
        return calories;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public MealType getType() {
        return type;
    }
}
