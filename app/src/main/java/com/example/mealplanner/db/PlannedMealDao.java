package com.example.mealplanner.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mealplanner.model.PlannedMeal;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PlannedMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlannedMeal(PlannedMeal plannedMeal);

    @Delete
    Completable deletePlannedMeal(PlannedMeal plannedMeal);

    // Get all planned meals (for efficient loading)
    @Query("SELECT * FROM planned_meals")
    Single<List<PlannedMeal>> getAllPlannedMeals();

    // Get planned meals for a specific date range if needed, or just filter in repo
    @Query("SELECT * FROM planned_meals WHERE date = :date")
    Single<List<PlannedMeal>> getPlannedMealsForDate(java.util.Date date);

    // Deletion by specific criteria
    @Query("DELETE FROM planned_meals WHERE date = :date AND type = :type")
    Completable deletePlannedMeal(java.util.Date date, com.example.mealplanner.model.MealType type);

    @Query("DELETE FROM planned_meals")
    Completable deleteAllPlannedMeals();
}
