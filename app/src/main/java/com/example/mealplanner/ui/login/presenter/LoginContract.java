package com.example.mealplanner.ui.login.presenter;

import android.view.View;

public interface LoginContract {
    interface View {
        void navigateToHome(android.view.View view);

        void navigateToGuest(android.view.View view);

        void navigateToSignup(android.view.View view);
    }

    interface Presenter {
        void onLoginClicked(android.view.View view);

        void onGuestClicked(android.view.View view);

        void onSignupPromptClicked(android.view.View view);
    }
}
