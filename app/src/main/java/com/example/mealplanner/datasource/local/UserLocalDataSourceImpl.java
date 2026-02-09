package com.example.mealplanner.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class UserLocalDataSourceImpl implements UserLocalDataSource {

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_REMEMBERED = "is_remembered";
    private final SharedPreferences sharedPreferences;

    public UserLocalDataSourceImpl(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Completable saveUserSession(boolean isRemembered) {
        return Completable.fromAction(() -> {
            sharedPreferences.edit().putBoolean(KEY_IS_REMEMBERED, isRemembered).apply();
        });
    }

    @Override
    public Single<Boolean> isUserRemembered() {
        return Single.fromCallable(() -> sharedPreferences.getBoolean(KEY_IS_REMEMBERED, false));
    }

    @Override
    public Completable clearUserSession() {
        return Completable.fromAction(() -> {
            sharedPreferences.edit().remove(KEY_IS_REMEMBERED).apply();
        });
    }
}
