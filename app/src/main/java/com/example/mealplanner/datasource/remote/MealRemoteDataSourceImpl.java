package com.example.mealplanner.datasource.remote;

import com.example.mealplanner.api.ApiClient;
import com.example.mealplanner.api.MealDbApiService;
import com.example.mealplanner.model.Category;
import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.api.MealApiModel;
import com.example.mealplanner.utils.ApiMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Single;

public class MealRemoteDataSourceImpl implements MealRemoteDataSource {
    private final MealDbApiService apiService;

    public MealRemoteDataSourceImpl() {
        this.apiService = ApiClient.getApiService();
    }

    @Override
    public Single<Meal> getRandomMeal() {
        return apiService.getRandomMeal()
                .map(response -> {
                    if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                        return ApiMapper.mapMealApiModelToMeal(response.getMeals().get(0));
                    }
                    throw new Exception("No meal found");
                });
    }

    @Override
    public Single<List<Meal>> searchMealsByName(String name) {
        return apiService.searchMealByName(name)
                .map(response -> {
                    if (response.getMeals() == null) {
                        return new ArrayList<Meal>();
                    }
                    List<Meal> meals = new ArrayList<>();
                    for (MealApiModel apiModel : response.getMeals()) {
                        meals.add(ApiMapper.mapMealApiModelToMeal(apiModel));
                    }
                    return meals;
                });
    }

    @Override
    public Single<List<Meal>> searchMealsByFirstLetter(String letter) {
        return apiService.searchMealByFirstLetter(letter)
                .map(response -> {
                    if (response.getMeals() == null) {
                        return new ArrayList<Meal>();
                    }
                    List<Meal> meals = new ArrayList<>();
                    for (MealApiModel apiModel : response.getMeals()) {
                        meals.add(ApiMapper.mapMealApiModelToMeal(apiModel));
                    }
                    return meals;
                });
    }

    @Override
    public Single<Meal> getMealById(String id) {
        return apiService.getMealById(id)
                .map(response -> {
                    if (response.getMeals() != null && !response.getMeals().isEmpty()) {
                        return ApiMapper.mapMealApiModelToMeal(response.getMeals().get(0));
                    }
                    throw new Exception("Meal not found");
                });
    }

    @Override
    public Single<List<Ingredient>> getAllIngredients() {
        return apiService.getAllIngredients()
                .map(response -> {
                    if (response.getMeals() == null) {
                        return new ArrayList<Ingredient>();
                    }
                    List<Ingredient> ingredients = new ArrayList<>();
                    for (var apiModel : response.getMeals()) {
                        ingredients.add(ApiMapper.mapIngredientApiModelToIngredient(apiModel));
                    }
                    return ingredients;
                });
    }

    @Override
    public Single<List<Country>> getAllCountries() {
        return apiService.getAllAreas()
                .map(response -> {
                    if (response.getMeals() == null) {
                        return new ArrayList<Country>();
                    }
                    List<Country> countries = new ArrayList<>();
                    for (var apiModel : response.getMeals()) {
                        countries.add(ApiMapper.mapAreaApiModelToCountry(apiModel));
                    }
                    return countries;
                });
    }

    @Override
    public Single<List<Category>> getCategories() {
        return apiService.getCategories()
                .map(response -> {
                    if (response.getCategories() == null) {
                        return new ArrayList<Category>();
                    }
                    List<Category> categories = new ArrayList<>();
                    for (var apiModel : response.getCategories()) {
                        categories.add(ApiMapper.mapCategoryApiModelToCategory(apiModel));
                    }
                    return categories;
                });
    }

    @Override
    public Single<List<Meal>> filterByIngredient(String ingredient) {
        return apiService.filterByIngredient(ingredient)
                .map(response -> {
                    if (response.getMeals() == null) {
                        return new ArrayList<Meal>();
                    }
                    List<Meal> meals = new ArrayList<>();
                    for (MealApiModel apiModel : response.getMeals()) {
                        meals.add(ApiMapper.mapMealApiModelToMeal(apiModel));
                    }
                    return meals;
                });
    }

    @Override
    public Single<List<Meal>> filterByArea(String area) {
        return apiService.filterByArea(area)
                .map(response -> {
                    if (response.getMeals() == null) {
                        return new ArrayList<Meal>();
                    }
                    List<Meal> meals = new ArrayList<>();
                    for (MealApiModel apiModel : response.getMeals()) {
                        meals.add(ApiMapper.mapMealApiModelToMeal(apiModel));
                    }
                    return meals;
                });
    }

    @Override
    public Single<List<Meal>> filterByCategory(String category) {
        return apiService.filterByCategory(category)
                .map(response -> {
                    if (response.getMeals() == null) {
                        return new ArrayList<Meal>();
                    }
                    List<Meal> meals = new ArrayList<>();
                    for (MealApiModel apiModel : response.getMeals()) {
                        meals.add(ApiMapper.mapMealApiModelToMeal(apiModel));
                    }
                    return meals;
                });
    }
}
