package com.example.mealplanner.ui.login.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mealplanner.R;
import com.example.mealplanner.ui.login.presenter.LoginContract;
import com.example.mealplanner.ui.login.presenter.LoginPresenter;

public class LoginFragment extends Fragment implements LoginContract.View {

        private LoginContract.Presenter presenter;

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

                // Navigation triggers
                view.findViewById(R.id.btn_login).setOnClickListener(v -> presenter.onLoginClicked(v));

                view.findViewById(R.id.btn_guest).setOnClickListener(v -> presenter.onGuestClicked(v));

                view.findViewById(R.id.tv_signup_prompt).setOnClickListener(v -> presenter.onSignupPromptClicked(v));
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
}


