package com.example.mealplanner.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mealplanner.R;

public class SignupFragment extends Fragment implements SignupContract.View {

    private SignupContract.Presenter presenter;

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

        view.findViewById(R.id.tv_login_prompt).setOnClickListener(v -> presenter.onLoginPromptClicked());

        view.findViewById(R.id.btn_signup).setOnClickListener(v -> presenter.onSignupClicked(v));
    }

    @Override
    public void navigateToHome(View view) {
        Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_homeFragment);
    }

    @Override
    public void navigateBack() {
        requireActivity().onBackPressed();
    }
}
