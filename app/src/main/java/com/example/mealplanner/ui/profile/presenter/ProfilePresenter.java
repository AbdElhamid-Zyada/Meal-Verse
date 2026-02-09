package com.example.mealplanner.ui.profile.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePresenter implements ProfileContract.Presenter {

    private final ProfileContract.View view;
    private final FirebaseAuth mAuth;

    public ProfilePresenter(ProfileContract.View view) {
        this.view = view;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            if (name == null || name.isEmpty()) {
                name = "No Name";
            }

            view.showUserData(name, email != null ? email : "No Email");
        } else {
            view.navigateToLogin();
        }
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
        mAuth.signOut();
        view.showMessage("Logged out successfully");
        view.navigateToLogin();
    }
}
