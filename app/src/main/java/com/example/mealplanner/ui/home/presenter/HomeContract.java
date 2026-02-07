package com.example.mealplanner.ui.home.presenter;

import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.Ingredient;
import java.util.List;

public interface HomeContract {
    interface View {
        void navigateToSearch();

        void navigateToMealDetails();

        void navigateToCountries();

        void navigateToIngredients();

        void showCountries(List<Country> countries);

        void showIngredients(List<Ingredient> ingredients);
    }

    interface Presenter {
        void onSearchClicked();

        void onMealOfDayClicked();

        void onSeeAllCountriesClicked();

        void onBrowseAllIngredientsClicked();

        void onSuggestedMealClicked();

        void onViewResumed();
    }
}
