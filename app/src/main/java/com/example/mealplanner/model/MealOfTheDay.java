package com.example.mealplanner.model;

public class MealOfTheDay {
    private String userId;
    private String mealId;
    private String date; // Format: yyyy-MM-dd
    private long timestamp;

    public MealOfTheDay() {
        // Required for Firestore
    }

    public MealOfTheDay(String userId, String mealId, String date, long timestamp) {
        this.userId = userId;
        this.mealId = mealId;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
