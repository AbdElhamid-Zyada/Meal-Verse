package com.example.mealplanner.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "planned_meals")
public class PlannedMeal {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String mealId;

    @NonNull
    private Date date;

    @NonNull
    private MealType type;

    public PlannedMeal(@NonNull String mealId, @NonNull Date date, @NonNull MealType type) {
        this.mealId = mealId;
        this.date = date;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getMealId() {
        return mealId;
    }

    public void setMealId(@NonNull String mealId) {
        this.mealId = mealId;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @NonNull
    public MealType getType() {
        return type;
    }

    public void setType(@NonNull MealType type) {
        this.type = type;
    }
}
