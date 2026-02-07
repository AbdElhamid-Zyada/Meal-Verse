package com.example.mealplanner;

import android.view.View;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View mView;

    public MainPresenter(MainContract.View view) {
        this.mView = view;
    }

    @Override
    public void onHomeClicked() {
        mView.navigateToHome();
    }

    @Override
    public void onPlannerClicked() {
        mView.navigateToPlanner();
    }

    @Override
    public void onSavedClicked() {
        mView.navigateToSaved();
    }

    @Override
    public void onProfileClicked() {
        mView.navigateToProfile();
    }

    @Override
    public void onDestinationChanged(int id) {
        if (id == R.id.splashFragment || id == R.id.loginFragment || id == R.id.signupFragment) {
            mView.setBottomNavVisibility(View.GONE);
        } else {
            mView.setBottomNavVisibility(View.VISIBLE);
            mView.updateBottomNavSelection(getNavItemIdForDestination(id));
        }
    }

    private int getNavItemIdForDestination(int destinationId) {
        if (destinationId == R.id.homeFragment)
            return R.id.nav_home;
        if (destinationId == R.id.plannerFragment)
            return R.id.nav_planner;
        if (destinationId == R.id.savedMealsFragment)
            return R.id.nav_saved;
        if (destinationId == R.id.profileFragment)
            return R.id.nav_profile;
        return -1;
    }
}
