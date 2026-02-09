package com.example.mealplanner.datasource.firestore;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface FirestoreDataSource {
    Completable saveMealOfTheDay(String userId, String mealId, String date);

    Single<String> getMealOfTheDayId(String userId, String date);
}
