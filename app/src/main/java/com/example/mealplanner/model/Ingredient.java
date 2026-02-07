package com.example.mealplanner.model;

public class Ingredient {
    private String name;
    private int imageResource;

    public Ingredient(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}
