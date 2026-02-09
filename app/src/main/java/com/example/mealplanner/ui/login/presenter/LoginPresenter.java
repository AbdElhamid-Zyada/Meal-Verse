package com.example.mealplanner.ui.login.presenter;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View mView;
    private FirebaseAuth mAuth;

    public LoginPresenter(LoginContract.View view) {
        this.mView = view;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onLoginClicked(android.view.View view, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            mView.showError("Email and password required");
            return;
        }
        mView.showLoading();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    mView.hideLoading();
                    if (task.isSuccessful()) {
                        mView.navigateToHome(view);
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
}
