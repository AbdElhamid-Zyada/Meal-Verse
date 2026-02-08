package com.example.mealplanner.model;

import androidx.room.TypeConverter;

public class MealTypeConverter {

    @TypeConverter
    public static MealType toMealType(String status) {
        if (status == null) {
            return null;
        }
        try {
            return MealType.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @TypeConverter
    public static String fromMealType(MealType type) {
        return type == null ? null : type.name();
    }
}
