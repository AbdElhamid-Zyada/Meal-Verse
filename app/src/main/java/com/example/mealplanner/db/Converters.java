package com.example.mealplanner.db;

import androidx.room.TypeConverter;
import com.example.mealplanner.model.MealType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {
    @TypeConverter
    public static MealType toMealType(String value) {
        if (value == null) {
            return MealType.DINNER; // Default
        }
        try {
            return MealType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return MealType.DINNER;
        }
    }

    @TypeConverter
    public static String fromMealType(MealType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null || value.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return new java.util.ArrayList<>(Arrays.asList(value.split(",")));
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }

    @TypeConverter
    public static java.util.Date fromTimestamp(Long value) {
        return value == null ? null : new java.util.Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(java.util.Date date) {
        return date == null ? null : date.getTime();
    }
}
