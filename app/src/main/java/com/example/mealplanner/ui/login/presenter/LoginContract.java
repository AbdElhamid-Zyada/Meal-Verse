package com.example.mealplanner.ui.login.presenter;

import android.view.View;

public interface LoginContract {
    interface View {
        void navigateToHome(android.view.View view);

        void navigateToGuest(android.view.View view);

        void navigateToSignup(android.view.View view);

        void showLoading();

        void hideLoading();

        void showError(String message);

        void startGoogleSignIn();
    }

    interface Presenter {
        void onLoginClicked(android.view.View view, String email, String password);

        void onGuestClicked(android.view.View view);

        void onSignupPromptClicked(android.view.View view);

        void onGoogleClicked(android.view.View view);

        void handleGoogleSignInResult(android.view.View view, String idToken);
    }
}
