package com.example.mealplanner.model;

public class Category {
    private String id;
    private String name;
    private String thumbnailUrl;
    private String description;

    public Category(String id, String name, String thumbnailUrl, String description) {
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }
}
