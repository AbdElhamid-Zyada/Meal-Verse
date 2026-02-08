package com.example.mealplanner.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mealplanner.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MealDao {

    @Query("SELECT * FROM meals")
    Observable<List<Meal>> getAllMeals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeal(Meal meal);

    @Delete
    Completable deleteMeal(Meal meal);

    @Query("SELECT EXISTS(SELECT 1 FROM meals WHERE id = :id)")
    Single<Boolean> isFavorite(String id);
}
