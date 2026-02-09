package com.example.mealplanner.ui.signup.presenter;

import android.view.View;

public interface SignupContract {
    interface View {
        void navigateToHome(android.view.View view);

        void navigateBack();

        void showLoading();

        void hideLoading();

        void showError(String message);
    }

    interface Presenter {
        void onSignupClicked(android.view.View view, String name, String email, String password);

        void onLoginPromptClicked();
    }
}
