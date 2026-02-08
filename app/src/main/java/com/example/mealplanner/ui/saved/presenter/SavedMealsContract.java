package com.example.mealplanner.ui.saved.presenter;

import com.example.mealplanner.model.Meal;
import java.util.List;

public interface SavedMealsContract {
    interface View {
        void showSavedMeals(List<Meal> meals);

        void showEmptyState();

        void showMessage(String message);

        void showLoading();

        void hideLoading();
    }

    interface Presenter {
        void loadSavedMeals();

        void removeMeal(Meal meal);

        void dispose();
    }
}
