package com.example.mealplanner.ui.profile.presenter;

public interface ProfileContract {
    interface View {
        void showEmail(String email);

        void navigateToHome();

        void navigateToPlanner();

        void navigateToFavorites(); // Saved Meals

        void navigateToLogin();

        void showMessage(String message);
    }

    interface Presenter {
        void loadUserProfile();

        void onHomeClicked();

        void onPlannerClicked();

        void onFavoritesClicked();

        void onLogoutClicked();
    }
}
