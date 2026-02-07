package com.example.mealplanner.ui.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mealplanner.R;
import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.ui.home.presenter.HomeContract;
import com.example.mealplanner.ui.home.presenter.HomePresenter;

import java.util.List;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment implements HomeContract.View {

        private HomeContract.Presenter presenter;
        private View root;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                        @Nullable Bundle savedInstanceState) {
                root = inflater.inflate(R.layout.fragment_home, container, false);
                return root;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                presenter = new HomePresenter(this);

                // Search Bar Click
                view.findViewById(R.id.card_search).setOnClickListener(v -> presenter.onSearchClicked());

                // Meal of the Day Click
                View mealCard = view.findViewById(R.id.card_meal_of_day);
                mealCard.setOnClickListener(v -> presenter.onMealOfDayClicked());
                view.findViewById(R.id.btn_next_meal).setOnClickListener(v -> presenter.onMealOfDayClicked());

                // Countries See All Click
                view.findViewById(R.id.tv_see_all_countries)
                                .setOnClickListener(v -> presenter.onSeeAllCountriesClicked());

                // Ingredients Browse All Click
                view.findViewById(R.id.tv_browse_all_ingredients)
                                .setOnClickListener(v -> presenter.onBrowseAllIngredientsClicked());

                // Suggested Meals Click
                view.findViewById(R.id.suggested_meal_1).setOnClickListener(v -> presenter.onSuggestedMealClicked());
                view.findViewById(R.id.suggested_meal_2).setOnClickListener(v -> presenter.onSuggestedMealClicked());
        }

        @Override
        public void onResume() {
                super.onResume();
                presenter.onViewResumed();
        }

        @Override
        public void navigateToSearch() {
                Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_searchFragment);
        }

        @Override
        public void navigateToMealDetails() {
                Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_mealDetailsFragment);
        }

        @Override
        public void navigateToCountries() {
                Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_countriesFragment);
        }

        @Override
        public void navigateToIngredients() {
                Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_ingredientsFragment);
        }

        @Override
        public void showCountries(List<Country> countries) {
                if (countries == null || countries.size() < 4)
                        return;

                // This is a static binding for the demo cards
                int[] containerIds = { R.id.country_container_1, R.id.country_container_2, R.id.country_container_3,
                                R.id.country_container_4 };
                for (int i = 0; i < 4; i++) {
                        View container = root.findViewById(containerIds[i]);
                        if (container != null) {
                                TextView tv = container.findViewWithTag("country_name");
                                ImageView iv = container.findViewWithTag("country_image");
                                if (tv != null)
                                        tv.setText(countries.get(i).getName());
                                // Image setting excluded for brevity as we use launcher background
                        }
                }
        }

        @Override
        public void showIngredients(List<Ingredient> ingredients) {
                if (ingredients == null || ingredients.size() < 4)
                        return;

                int[] containerIds = { R.id.ingredient_container_1, R.id.ingredient_container_2,
                                R.id.ingredient_container_3, R.id.ingredient_container_4 };
                for (int i = 0; i < 4; i++) {
                        View container = root.findViewById(containerIds[i]);
                        if (container != null) {
                                TextView tv = container.findViewWithTag("ingredient_name");
                                ImageView iv = container.findViewWithTag("ingredient_image");
                                if (tv != null)
                                        tv.setText(ingredients.get(i).getName());
                        }
                }
        }
}
