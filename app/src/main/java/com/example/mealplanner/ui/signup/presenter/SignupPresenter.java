package com.example.mealplanner.ui.signup.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupPresenter implements SignupContract.Presenter {
    private SignupContract.View mView;
    private FirebaseAuth mAuth;

    public SignupPresenter(SignupContract.View view) {
        this.mView = view;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onSignupClicked(android.view.View view, String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mView.showError("All fields are required");
            return;
        }

        mView.showLoading();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update profile with name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        task.getResult().getUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(updateTask -> {
                                    mView.hideLoading();
                                    mView.navigateToHome(view);
                                });
                    } else {
                        mView.hideLoading();
                        mView.showError(
                                task.getException() != null ? task.getException().getMessage() : "Signup failed");
                    }
                });
    }

    @Override
    public void onLoginPromptClicked() {
        mView.navigateBack();
    }
}
