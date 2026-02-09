package com.example.mealplanner.ui.signup.view;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.Arrays;

import com.example.mealplanner.R;
import com.example.mealplanner.ui.signup.presenter.SignupContract;
import com.example.mealplanner.ui.signup.presenter.SignupPresenter;

public class SignupFragment extends Fragment implements SignupContract.View {

    private SignupContract.Presenter presenter;
    private EditText etName, etEmail, etPassword;
    private ProgressBar progressBar;
    private NavController navController;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new SignupPresenter(this);

        // Initialize Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Activity Result Launcher for Google
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                });

        // Find views - using the EditText inside RelativeLayouts
        etName = view.findViewById(R.id.form_container).findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.form_container).findViewById(R.id.et_email_signup);
        etPassword = view.findViewById(R.id.form_container).findViewById(R.id.et_password_signup);
        progressBar = view.findViewById(R.id.progress_bar_signup);

        view.findViewById(R.id.tv_login_prompt).setOnClickListener(v -> presenter.onLoginPromptClicked());

        view.findViewById(R.id.btn_signup).setOnClickListener(v -> {
            String name = etName != null ? etName.getText().toString().trim() : "";
            String email = etEmail != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword != null ? etPassword.getText().toString().trim() : "";
            presenter.onSignupClicked(v, name, email, password);
        });

        view.findViewById(R.id.btn_google_s).setOnClickListener(v -> presenter.onGoogleClicked(v));
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
        Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_homeFragment);
    }

    @Override
    public void navigateBack() {
        if (isAdded() && getActivity() != null) {
            requireActivity().onBackPressed();
        }
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
