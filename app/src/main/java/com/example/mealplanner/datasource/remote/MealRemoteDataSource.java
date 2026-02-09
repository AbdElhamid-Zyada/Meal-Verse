package com.example.mealplanner.datasource.remote;

import com.example.mealplanner.model.Category;
import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface MealRemoteDataSource {
    Single<Meal> getRandomMeal();

    Single<List<Meal>> searchMealsByName(String name);

    Single<List<Meal>> searchMealsByFirstLetter(String letter);

    Single<Meal> getMealById(String id);

    Single<List<Ingredient>> getAllIngredients();

    Single<List<Country>> getAllCountries();

    Single<List<Category>> getCategories();

    Single<List<Meal>> filterByIngredient(String ingredient);

    Single<List<Meal>> filterByArea(String area);

    Single<List<Meal>> filterByCategory(String category);
}
