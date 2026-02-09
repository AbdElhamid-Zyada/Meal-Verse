package com.example.mealplanner.datasource.firestore;

import com.example.mealplanner.model.MealType;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface FirestoreDataSource {
    Completable saveMealOfTheDay(String userId, String mealId, String date);

    Single<String> getMealOfTheDayId(String userId, String date);

    // Saved Meals
    Completable saveFavoriteMeal(String userId, String mealId);

    Completable removeFavoriteMeal(String userId, String mealId);

    Single<List<String>> getFavoriteMealIds(String userId);

    // Planned Meals
    Completable savePlannedMeal(String userId, String mealId, Date date, MealType type);

    Completable removePlannedMeal(String userId, String mealId, Date date, MealType type);

    Single<List<PlannedMealFirestore>> getPlannedMealsForWeek(String userId, Date startDate, Date endDate);
}
