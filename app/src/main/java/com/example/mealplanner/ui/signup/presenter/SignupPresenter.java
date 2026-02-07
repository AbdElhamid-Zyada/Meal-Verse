package com.example.mealplanner.ui.signup.presenter;

import android.view.View;

public class SignupPresenter implements SignupContract.Presenter {
    private SignupContract.View mView;

    public SignupPresenter(SignupContract.View view) {
        this.mView = view;
    }

    @Override
    public void onSignupClicked(View view) {
        mView.navigateToHome(view);
    }

    @Override
    public void onLoginPromptClicked() {
        mView.navigateBack();
    }
}
