package com.example.mealplanner.repository;

import android.content.Context;

import com.example.mealplanner.datasource.local.UserLocalDataSource;
import com.example.mealplanner.datasource.local.UserLocalDataSourceImpl;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class UserRepositoryImpl implements UserRepository {

    private static UserRepositoryImpl instance;
    private final UserLocalDataSource localDataSource;

    private UserRepositoryImpl(Context context) {
        this.localDataSource = new UserLocalDataSourceImpl(context);
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
    public Completable logout() {
        return localDataSource.clearUserSession();
    }
}
