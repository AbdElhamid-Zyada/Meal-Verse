package com.example.mealplanner.repository;

import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.FilterType;
import com.example.mealplanner.model.Ingredient;
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

    // Ingredients methods
    Observable<List<Ingredient>> getIngredients();

    Observable<List<Ingredient>> searchIngredients(String query);

    // Countries methods
    Observable<List<Country>> getCountries();

    Observable<List<Country>> searchCountries(String query);

    // Search & Filter methods
    Observable<List<Meal>> searchMeals(String query, String category, String area, String ingredient);

    Observable<List<String>> getFilterOptions(FilterType type);

    Completable addMealToPlan(Meal meal, Date date, MealType type);

    // Details method
    Single<Meal> getMealDetails(String mealId);
}
