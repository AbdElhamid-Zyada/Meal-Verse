package com.example.mealplanner.ui.profile;

public class ProfilePresenter implements ProfileContract.Presenter {
    private ProfileContract.View mView;

    public ProfilePresenter(ProfileContract.View view) {
        this.mView = view;
    }
}
