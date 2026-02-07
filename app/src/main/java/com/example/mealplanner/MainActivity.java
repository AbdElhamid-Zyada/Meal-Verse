package com.example.mealplanner;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private MainContract.Presenter presenter;
    private NavController navController;
    private View bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);
        bottomNav = findViewById(R.id.bottom_nav_card);
        View mainView = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Wire up Bottom Navigation
            findViewById(R.id.nav_home).setOnClickListener(v -> presenter.onHomeClicked());
            findViewById(R.id.nav_planner).setOnClickListener(v -> presenter.onPlannerClicked());
            findViewById(R.id.nav_saved).setOnClickListener(v -> presenter.onSavedClicked());
            findViewById(R.id.nav_profile).setOnClickListener(v -> presenter.onProfileClicked());

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                presenter.onDestinationChanged(destination.getId());
            });
        }
    }

    @Override
    public void navigateToHome() {
        if (navController != null)
            navController.navigate(R.id.homeFragment);
    }

    @Override
    public void navigateToPlanner() {
        if (navController != null)
            navController.navigate(R.id.plannerFragment);
    }

    @Override
    public void navigateToSaved() {
        if (navController != null)
            navController.navigate(R.id.savedMealsFragment);
    }

    @Override
    public void navigateToProfile() {
        if (navController != null)
            navController.navigate(R.id.profileFragment);
    }

    @Override
    public void setBottomNavVisibility(int visibility) {
        bottomNav.setVisibility(visibility);
    }

    @Override
    public void updateBottomNavSelection(int selectedItemId) {
        int primaryColor = getResources().getColor(R.color.primary);
        int grayColor = getResources().getColor(R.color.gray_800);

        updateItemUI(R.id.nav_home, selectedItemId == R.id.nav_home, primaryColor, grayColor);
        updateItemUI(R.id.nav_planner, selectedItemId == R.id.nav_planner, primaryColor, grayColor);
        updateItemUI(R.id.nav_saved, selectedItemId == R.id.nav_saved, primaryColor, grayColor);
        updateItemUI(R.id.nav_profile, selectedItemId == R.id.nav_profile, primaryColor, grayColor);
    }

    private void updateItemUI(int itemId, boolean isSelected, int activeColor, int inactiveColor) {
        View item = findViewById(itemId);
        if (item instanceof android.widget.LinearLayout) {
            android.widget.LinearLayout layout = (android.widget.LinearLayout) item;
            int color = isSelected ? activeColor : inactiveColor;

            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                if (child instanceof android.widget.ImageView) {
                    ((android.widget.ImageView) child).setColorFilter(color);
                } else if (child instanceof android.widget.TextView) {
                    ((android.widget.TextView) child).setTextColor(color);
                    ((android.widget.TextView) child).setTypeface(null,
                            isSelected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
                }
            }
        }
    }
}