package com.example.mealplanner;

public interface MainContract {
    interface View {
        void navigateToHome();

        void navigateToPlanner();

        void navigateToSaved();

        void navigateToProfile();

        void setBottomNavVisibility(int visibility);

        void updateBottomNavSelection(int selectedItemId);
    }

    interface Presenter {
        void onHomeClicked();

        void onPlannerClicked();

        void onSavedClicked();

        void onProfileClicked();

        void onDestinationChanged(int destinationId);
    }
}
