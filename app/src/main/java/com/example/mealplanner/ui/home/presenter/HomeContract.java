package com.example.mealplanner.ui.home.presenter;

import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.model.Meal;
import java.util.List;

public interface HomeContract {
    interface View {
        void navigateToSearch();

        void navigateToMealDetails(String mealId);

        void navigateToCountries();

        void navigateToIngredients();

        void showCountries(List<Country> countries);

        void showIngredients(List<Ingredient> ingredients);

        void showLoading();

        void hideLoading();

        void displayMealOfTheDay(Meal meal);

        void showMealOfTheDayError(String message);

        void displaySuggestedMeals(List<Meal> meals);
    }

    interface Presenter {
        void onSearchClicked();

        void onMealOfDayClicked();

        void onSeeAllCountriesClicked();

        void onBrowseAllIngredientsClicked();

        void onViewResumed();

        void loadMealOfTheDay();

        void onMealOfTheDayCardClicked(Meal meal);

        void loadSuggestedMeals();

        void onSuggestedMealClicked(Meal meal);

        void onDestroy();
    }
}
