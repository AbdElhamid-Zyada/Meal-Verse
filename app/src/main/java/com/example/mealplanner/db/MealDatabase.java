package com.example.mealplanner.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.PlannedMeal;
import com.example.mealplanner.db.PlannedMealDao;

@Database(entities = { Meal.class, PlannedMeal.class }, version = 6, exportSchema = false)
@TypeConverters({ Converters.class })
public abstract class MealDatabase extends RoomDatabase {

    private static volatile MealDatabase INSTANCE;

    public abstract MealDao mealDao();

    public abstract PlannedMealDao plannedMealDao();

    public static MealDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MealDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MealDatabase.class, "meal_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
