package com.example.mealplanner.repository;

import android.content.Context;

import com.example.mealplanner.datasource.local.UserLocalDataSource;
import com.example.mealplanner.datasource.local.UserLocalDataSourceImpl;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class UserRepositoryImpl implements UserRepository {

    private static UserRepositoryImpl instance;
    private final UserLocalDataSource localDataSource;
    private final Context context;

    private UserRepositoryImpl(Context context) {
        this.context = context.getApplicationContext();
        this.localDataSource = new UserLocalDataSourceImpl(this.context);
    }

    public static synchronized UserRepositoryImpl getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepositoryImpl(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public Completable setUserRemembered(boolean isRemembered) {
        return localDataSource.saveUserSession(isRemembered);
    }

    @Override
    public Single<Boolean> isUserRemembered() {
        return localDataSource.isUserRemembered();
    }

    @Override
    public Completable saveGuestMode(boolean isGuest) {
        return localDataSource.saveGuestMode(isGuest);
    }

    @Override
    public Single<Boolean> isGuestMode() {
        return localDataSource.isGuestMode();
    }

    @Override
    public Completable saveGuestMeal(String mealId, long timestamp) {
        return localDataSource.saveGuestMeal(mealId, timestamp);
    }

    @Override
    public Single<androidx.core.util.Pair<String, Long>> getGuestMeal() {
        return localDataSource.getGuestMeal();
    }

    @Override
    public Completable logout() {
        // Clear all meal data from Room database before clearing user session
        MealRepository mealRepository = MealRepositoryImpl.getInstance();
        return mealRepository.clearAllUserData()
                .andThen(localDataSource.clearUserSession());
    }
}
