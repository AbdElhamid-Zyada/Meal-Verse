package com.example.mealplanner.ui.login.presenter;

import android.view.View;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View mView;
    private FirebaseAuth mAuth;
    private com.example.mealplanner.repository.UserRepository userRepository;
    private com.example.mealplanner.repository.MealRepository mealRepository;
    private io.reactivex.rxjava3.disposables.CompositeDisposable disposables = new io.reactivex.rxjava3.disposables.CompositeDisposable();

    public LoginPresenter(LoginContract.View view, com.example.mealplanner.repository.UserRepository userRepository) {
        this.mView = view;
        this.mAuth = FirebaseAuth.getInstance();
        this.userRepository = userRepository;
        this.mealRepository = com.example.mealplanner.repository.MealRepositoryImpl.getInstance();
    }

    @Override
    public void onLoginClicked(View view, String email, String password, boolean isRememberMeChecked) {
        if (email.isEmpty() || password.isEmpty()) {
            mView.showError("Please fill all fields");
            return;
        }

        mView.showLoading();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    mView.hideLoading();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Sync data from Firestore after successful login
                            String userId = user.getUid();
                            System.out.println("[LOGIN] User logged in successfully: " + userId);

                            // Save remember me preference
                            if (isRememberMeChecked) {
                                disposables.add(userRepository.setUserRemembered(true)
                                        .andThen(userRepository.saveGuestMode(false))
                                        .andThen(mealRepository.syncSavedMealsFromFirestore(userId))
                                        .andThen(mealRepository.syncPlannedMealsFromFirestore(userId))
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> {
                                                    System.out.println("[LOGIN] Sync completed, navigating to home");
                                                    mView.navigateToHome(view);
                                                },
                                                throwable -> {
                                                    System.err.println(
                                                            "[LOGIN ERROR] Sync failed: " + throwable.getMessage());
                                                    throwable.printStackTrace();
                                                    // Navigate anyway, local data is available
                                                    mView.navigateToHome(view);
                                                }));
                            } else {
                                // Default is not remembered, or explicitly clear if previously set?
                                // Requirement is "if checked keep logged in". If not checked, session is
                                // temporary.
                                // We can clear it to be safe or just not set it.
                                // Let's ensure we clear it if unchecked to be consistent.
                                disposables.add(userRepository.setUserRemembered(false)
                                        .andThen(userRepository.saveGuestMode(false))
                                        .andThen(mealRepository.syncSavedMealsFromFirestore(userId))
                                        .andThen(mealRepository.syncPlannedMealsFromFirestore(userId))
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> {
                                                    System.out.println("[LOGIN] Sync completed, navigating to home");
                                                    mView.navigateToHome(view);
                                                },
                                                throwable -> {
                                                    System.err.println(
                                                            "[LOGIN ERROR] Sync failed: " + throwable.getMessage());
                                                    throwable.printStackTrace();
                                                    // Navigate anyway, local data is available
                                                    mView.navigateToHome(view);
                                                }));
                            }
                        }
                    } else {
                        mView.showError(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void onGuestClicked(View view) {
        disposables.add(userRepository.saveGuestMode(true)
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(
                        () -> mView.navigateToHome(view), // Navigate directly to Home as per user request flow implies
                                                          // full app access
                        throwable -> mView.showError("Failed to set guest mode")));
    }

    @Override
    public void onSignupPromptClicked(View view) {
        mView.navigateToSignup(view);
    }

    @Override
    public void onGoogleClicked(View view) {
        mView.startGoogleSignIn();
    }

    @Override
    public void handleGoogleSignInResult(View view, String idToken, boolean isRememberMeChecked) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        signInWithCredential(view, credential, isRememberMeChecked);
    }

    private void signInWithCredential(View view, AuthCredential credential, boolean isRememberMeChecked) {
        mView.showLoading();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    mView.hideLoading();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Sync data from Firestore after successful Google login
                            String userId = user.getUid();
                            System.out.println("[LOGIN] Google user logged in successfully: " + userId);

                            if (isRememberMeChecked) {
                                disposables.add(userRepository.setUserRemembered(true)
                                        .andThen(userRepository.saveGuestMode(false))
                                        .andThen(mealRepository.syncSavedMealsFromFirestore(userId))
                                        .andThen(mealRepository.syncPlannedMealsFromFirestore(userId))
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> {
                                                    System.out.println(
                                                            "[LOGIN] Google sync completed, navigating to home");
                                                    mView.navigateToHome(view);
                                                },
                                                throwable -> {
                                                    System.err.println("[LOGIN ERROR] Google sync failed: "
                                                            + throwable.getMessage());
                                                    throwable.printStackTrace();
                                                    // Navigate anyway, local data is available
                                                    mView.navigateToHome(view);
                                                }));
                            } else {
                                disposables.add(userRepository.setUserRemembered(false)
                                        .andThen(userRepository.saveGuestMode(false))
                                        .andThen(mealRepository.syncSavedMealsFromFirestore(userId))
                                        .andThen(mealRepository.syncPlannedMealsFromFirestore(userId))
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> {
                                                    System.out.println(
                                                            "[LOGIN] Google sync completed, navigating to home");
                                                    mView.navigateToHome(view);
                                                },
                                                throwable -> {
                                                    System.err.println("[LOGIN ERROR] Google sync failed: "
                                                            + throwable.getMessage());
                                                    throwable.printStackTrace();
                                                    // Navigate anyway, local data is available
                                                    mView.navigateToHome(view);
                                                }));
                            }
                        }
                    } else {
                        mView.showError(task.getException() != null ? task.getException().getMessage()
                                : "Authentication failed");
                    }
                });
    }
}
