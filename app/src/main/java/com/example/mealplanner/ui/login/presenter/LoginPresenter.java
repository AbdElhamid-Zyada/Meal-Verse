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
    private io.reactivex.rxjava3.disposables.CompositeDisposable disposables = new io.reactivex.rxjava3.disposables.CompositeDisposable();

    public LoginPresenter(LoginContract.View view, com.example.mealplanner.repository.UserRepository userRepository) {
        this.mView = view;
        this.mAuth = FirebaseAuth.getInstance();
        this.userRepository = userRepository;
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
                            // Save remember me preference
                            if (isRememberMeChecked) {
                                disposables.add(userRepository.setUserRemembered(true)
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> mView.navigateToHome(view),
                                                throwable -> mView.showError("Failed to save preference")));
                            } else {
                                // Default is not remembered, or explicitly clear if previously set?
                                // Requirement is "if checked keep logged in". If not checked, session is
                                // temporary.
                                // We can clear it to be safe or just not set it.
                                // Let's ensure we clear it if unchecked to be consistent.
                                disposables.add(userRepository.setUserRemembered(false) // Or logout logic
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> mView.navigateToHome(view),
                                                throwable -> mView.showError("Failed to save preference")));
                            }
                        }
                    } else {
                        mView.showError(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void onGuestClicked(View view) {
        mView.navigateToGuest(view);
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
                            if (isRememberMeChecked) {
                                disposables.add(userRepository.setUserRemembered(true)
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> mView.navigateToHome(view),
                                                throwable -> mView.showError("Failed to save preference")));
                            } else {
                                disposables.add(userRepository.setUserRemembered(false)
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(
                                                io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                () -> mView.navigateToHome(view),
                                                throwable -> mView.showError("Failed to save preference")));
                            }
                        }
                    } else {
                        mView.showError(task.getException() != null ? task.getException().getMessage()
                                : "Authentication failed");
                    }
                });
    }
}
