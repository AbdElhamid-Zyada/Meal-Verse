package com.example.mealplanner.ui.signup;

import android.view.View;

public interface SignupContract {
    interface View {
        void navigateToHome(android.view.View view);

        void navigateBack();
    }

    interface Presenter {
        void onSignupClicked(android.view.View view);

        void onLoginPromptClicked();
    }
}
