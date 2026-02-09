package com.example.mealplanner.ui.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mealplanner.R;
import com.example.mealplanner.ui.profile.presenter.ProfileContract;
import com.example.mealplanner.ui.profile.presenter.ProfilePresenter;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private ProfileContract.Presenter presenter;
    private TextView tvName, tvEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new ProfilePresenter(this);

        tvName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_user_email);

        // Setup Navigation Items
        setupNavItem(view.findViewById(R.id.btn_nav_home), "Home Page", R.drawable.ic_home,
                v -> presenter.onHomeClicked());
        setupNavItem(view.findViewById(R.id.btn_nav_planner), "My Weekly Plan", R.drawable.ic_calendar_filled,
                v -> presenter.onPlannerClicked());
        setupNavItem(view.findViewById(R.id.btn_nav_favorites), "Favorite Meals", R.drawable.ic_favorite,
                v -> presenter.onFavoritesClicked());

        // Logout
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> presenter.onLogoutClicked());

        presenter.loadUserProfile();
    }

    private void setupNavItem(View itemView, String title, int iconRes, View.OnClickListener listener) {
        TextView tvTitle = itemView.findViewById(R.id.tv_nav_title);
        ImageView ivIcon = itemView.findViewById(R.id.iv_nav_icon);

        tvTitle.setText(title);
        ivIcon.setImageResource(iconRes);

        itemView.setOnClickListener(listener);
    }

    @Override
    public void showUserData(String name, String email) {
        if (tvName != null) {
            tvName.setText(name);
        }
        if (tvEmail != null) {
            tvEmail.setText(email);
        }
    }

    @Override
    public void navigateToHome() {
        Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
    }

    @Override
    public void navigateToPlanner() {
        Navigation.findNavController(requireView()).navigate(R.id.plannerFragment);
    }

    @Override
    public void navigateToFavorites() {
        Navigation.findNavController(requireView()).navigate(R.id.savedMealsFragment);
    }

    @Override
    public void navigateToLogin() {
        Navigation.findNavController(requireView()).navigate(R.id.loginFragment);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
