package com.example.mealplanner.ui.details.presenter;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import java.util.Date;
import java.util.List;

public interface MealDetailsContract {
    interface View {
        void showMealDetails(Meal meal);

        void showIngredients(List<String> ingredients, List<String> measures);

        void showInstructions(List<String> steps);

        void showVideo(String videoId);

        void setFavoriteState(boolean isFavorite);

        void showMessage(String message);

        void showOfflineBadge(boolean isVisible);

        // Planning Dialogs
        void showDatePicker(List<Date> availableDates, Meal meal);

        void showMealTypePicker(Meal meal, Date date);
    }

    interface Presenter {
        void loadMealDetails(String mealId);

        void onFavoriteClicked();

        void onAddToPlanClicked();

        // Planning logic (reused)
        void onDateSelected(Date date);

        void onMealTypeSelected(Date date, MealType type);

        void dispose();
    }
}
