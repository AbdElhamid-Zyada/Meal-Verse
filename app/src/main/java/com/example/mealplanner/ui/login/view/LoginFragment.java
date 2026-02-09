package com.example.mealplanner.ui.login.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.util.Log;

import com.example.mealplanner.R;
import com.example.mealplanner.ui.login.presenter.LoginContract;
import com.example.mealplanner.ui.login.presenter.LoginPresenter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.Arrays;

public class LoginFragment extends Fragment implements LoginContract.View {

        private LoginContract.Presenter presenter;
        private EditText etEmail, etPassword;
        private ProgressBar progressBar;
        private GoogleSignInClient mGoogleSignInClient;
        private ActivityResultLauncher<Intent> googleSignInLauncher;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                        @Nullable Bundle savedInstanceState) {
                return inflater.inflate(R.layout.fragment_login, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                presenter = new LoginPresenter(this);

                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build();
                mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

                // Activity Result Launcher for Google
                googleSignInLauncher = registerForActivityResult(
                                new ActivityResultContracts.StartActivityForResult(),
                                result -> {
                                        Task<GoogleSignInAccount> task = GoogleSignIn
                                                        .getSignedInAccountFromIntent(result.getData());
                                        handleGoogleSignInResult(task);
                                });

                etEmail = view.findViewById(R.id.et_email);
                etPassword = view.findViewById(R.id.et_password);
                progressBar = view.findViewById(R.id.progress_bar);

                view.findViewById(R.id.btn_login).setOnClickListener(v -> {
                        String email = etEmail.getText().toString().trim();
                        String password = etPassword.getText().toString().trim();
                        presenter.onLoginClicked(v, email, password);
                });

                view.findViewById(R.id.btn_guest).setOnClickListener(v -> presenter.onGuestClicked(v));

                view.findViewById(R.id.tv_signup_prompt).setOnClickListener(v -> presenter.onSignupPromptClicked(v));
                view.findViewById(R.id.tv_signup_prompt2).setOnClickListener(v -> presenter.onSignupPromptClicked(v));

                view.findViewById(R.id.btn_google).setOnClickListener(v -> presenter.onGoogleClicked(v));
        }

        private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
                try {
                        GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                        presenter.handleGoogleSignInResult(getView(), account.getIdToken());
                } catch (ApiException e) {
                        showError("Google sign in failed: " + e.getMessage());
                }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void navigateToHome(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
        }

        @Override
        public void navigateToGuest(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_guestFragment);
        }

        @Override
        public void navigateToSignup(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment);
        }

        @Override
        public void startGoogleSignIn() {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
        }

        @Override
        public void showLoading() {
                if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                }
        }

        @Override
        public void hideLoading() {
                if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                }
        }

        @Override
        public void showError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
}
