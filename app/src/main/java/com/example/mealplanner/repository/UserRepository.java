package com.example.mealplanner.repository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {
    Completable setUserRemembered(boolean isRemembered);

    Single<Boolean> isUserRemembered();

    Completable saveGuestMode(boolean isGuest);

    Single<Boolean> isGuestMode();

    Completable logout();
}
