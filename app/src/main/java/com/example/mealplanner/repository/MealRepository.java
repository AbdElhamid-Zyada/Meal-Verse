package com.example.mealplanner.repository;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface MealRepository {
    // Planner methods (Keep existing mock methods for now)
    Map<MealType, Meal> getMealsForDay(Date date);

    void deleteMeal(Date date, MealType type);

    // Favorites methods (RxJava + Room)
    Observable<List<Meal>> getFavoriteMeals();

    Completable addFavorite(Meal meal);

    Completable removeFavorite(Meal meal);

    Single<Boolean> isFavorite(String mealId);
    // Add other methods as needed for planning meals
}
