package com.example.mealplanner.api;

import com.example.mealplanner.model.api.AreasResponse;
import com.example.mealplanner.model.api.CategoriesResponse;
import com.example.mealplanner.model.api.IngredientsResponse;
import com.example.mealplanner.model.api.MealsResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealDbApiService {

    @GET("random.php")
    Single<MealsResponse> getRandomMeal();

    @GET("search.php")
    Single<MealsResponse> searchMealByName(@Query("s") String name);

    @GET("search.php")
    Single<MealsResponse> searchMealByFirstLetter(@Query("f") String letter);

    @GET("lookup.php")
    Single<MealsResponse> getMealById(@Query("i") String id);

    @GET("list.php?i=list")
    Single<IngredientsResponse> getAllIngredients();

    @GET("list.php?a=list")
    Single<AreasResponse> getAllAreas();

    @GET("categories.php")
    Single<CategoriesResponse> getCategories();

    @GET("filter.php")
    Single<MealsResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("filter.php")
    Single<MealsResponse> filterByArea(@Query("a") String area);

    @GET("filter.php")
    Single<MealsResponse> filterByCategory(@Query("c") String category);
}
