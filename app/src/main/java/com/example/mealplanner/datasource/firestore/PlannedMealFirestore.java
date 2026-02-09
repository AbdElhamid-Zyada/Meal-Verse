package com.example.mealplanner.datasource.firestore;

import com.example.mealplanner.model.MealType;

import java.util.Date;

public class PlannedMealFirestore {
    private String mealId;
    private Date date;
    private MealType type;

    public PlannedMealFirestore() {
        // Required for Firestore deserialization
    }

    public PlannedMealFirestore(String mealId, Date date, MealType type) {
        this.mealId = mealId;
        this.date = date;
        this.type = type;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }
}
