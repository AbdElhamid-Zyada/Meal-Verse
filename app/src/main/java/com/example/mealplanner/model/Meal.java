package com.example.mealplanner.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "meals")
public class Meal implements Serializable {
    @PrimaryKey
    @androidx.annotation.NonNull
    private String id;
    private String name;
    private int imageResId; // Using resource ID for mock data, normally would be URL
    private String imageUrl; // For API data
    private int calories;
    private int preparationTimeMinutes;
    private String category;
    private String area;
    private java.util.List<String> ingredients;
    private MealType type;
    private boolean isFavorite; // Now stored in Room to distinguish favorites from cache

    // Room needs a no-arg constructor if other constructors don't match perfectly
    public Meal() {
    }

    public Meal(String id, String name, int imageResId, int calories, int preparationTimeMinutes, MealType type) {
        this(id, name, imageResId, calories, preparationTimeMinutes, type, "Miscellaneous", "Unknown",
                new java.util.ArrayList<>());
    }

    public Meal(String id, String name, int imageResId, int calories, int preparationTimeMinutes, MealType type,
            String category, String area, java.util.List<String> ingredients) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.calories = calories;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.type = type;
        this.category = category;
        this.area = area;
        this.ingredients = ingredients;
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

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    public java.util.List<String> getIngredients() {
        return ingredients;
    }

    // Setters for Room
    public void setId(@androidx.annotation.NonNull String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setPreparationTimeMinutes(int preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setIngredients(java.util.List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // New fields for Details Screen
    private String instructions;
    private String youtubeUrl;
    private String sourceUrl;
    private java.util.List<String> measures;

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public java.util.List<String> getMeasures() {
        return measures;
    }

    public void setMeasures(java.util.List<String> measures) {
        this.measures = measures;
    }
}
