package com.example.mealplanner.ui.profile.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePresenter implements ProfileContract.Presenter {

    private final ProfileContract.View view;
    private final FirebaseAuth mAuth;
    private final com.example.mealplanner.repository.UserRepository userRepository;
    private final io.reactivex.rxjava3.disposables.CompositeDisposable disposables = new io.reactivex.rxjava3.disposables.CompositeDisposable();

    public ProfilePresenter(ProfileContract.View view,
            com.example.mealplanner.repository.UserRepository userRepository) {
        this.view = view;
        this.mAuth = FirebaseAuth.getInstance();
        this.userRepository = userRepository;
    }

    @Override
    public void loadUserProfile() {
        disposables.add(userRepository.isGuestMode()
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(
                        isGuest -> {
                            if (isGuest) {
                                view.showUserData("GUEST", "");
                            } else {
                                loadAuthenticatedUserProfile();
                            }
                        },
                        error -> view.showMessage("Error checking guest status")));
    }

    private void loadAuthenticatedUserProfile() {
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
        disposables.add(userRepository.logout()
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            mAuth.signOut();
                            view.showMessage("Logged out successfully");
                            view.navigateToLogin();
                        },
                        error -> view.showMessage("Error logging out: " + error.getMessage())));
    }

    public void dispose() {
        disposables.clear();
    }
}
