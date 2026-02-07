package com.example.mealplanner.ui.login.presenter;

import android.view.View;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View mView;

    public LoginPresenter(LoginContract.View view) {
        this.mView = view;
    }

    @Override
    public void onLoginClicked(View view) {
        mView.navigateToHome(view);
    }

    @Override
    public void onGuestClicked(View view) {
        mView.navigateToGuest(view);
    }

    @Override
    public void onSignupPromptClicked(View view) {
        mView.navigateToSignup(view);
    }
}
