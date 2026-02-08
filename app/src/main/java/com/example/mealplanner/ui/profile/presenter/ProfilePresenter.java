package com.example.mealplanner.ui.profile.presenter;

public class ProfilePresenter implements ProfileContract.Presenter {

    private final ProfileContract.View view;

    public ProfilePresenter(ProfileContract.View view) {
        this.view = view;
    }

    @Override
    public void loadUserProfile() {
        // Mock fetching user email.
        // In real app, use FirebaseAuth.getInstance().getCurrentUser().getEmail()
        // Checking if we can use a mock user or if there is a centralized UserSession
        // For now, hardcode as per request "alex.johnson@example.com" or similar from
        // screenshot
        // User request: "only keep the email that will be fetched from the user email
        // that will log in"

        String email = "alex.johnson@example.com";
        view.showEmail(email);
    }

    @Override
    public void onHomeClicked() {
        view.navigateToHome();
    }

    @Override
    public void onPlannerClicked() {
        view.navigateToPlanner();
    }

    @Override
    public void onFavoritesClicked() {
        view.navigateToFavorites();
    }

    @Override
    public void onLogoutClicked() {
        // Perform logout logic (e.g., FirebaseAuth.getInstance().signOut())
        // Then navigate to login
        view.showMessage("Logged out successfully");
        view.navigateToLogin();
    }
}
