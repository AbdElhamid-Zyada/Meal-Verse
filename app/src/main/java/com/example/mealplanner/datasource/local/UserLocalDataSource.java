package com.example.mealplanner.datasource.local;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface UserLocalDataSource {
    Completable saveUserSession(boolean isRemembered);

    Single<Boolean> isUserRemembered();

    Completable clearUserSession();
}
