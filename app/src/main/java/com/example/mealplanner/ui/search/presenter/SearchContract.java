package com.example.mealplanner.ui.search.presenter;

import com.example.mealplanner.model.FilterType;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;

import java.util.Date;
import java.util.List;

public interface SearchContract {
    interface View {
        void showSearchResults(List<Meal> meals);

        void showResultCount(int count);

        void showFilterOptions(List<String> options, FilterType type);

        void addFilterChip(String label, FilterType type);

        void removeFilterChip(FilterType type);

        void clearAllChips();

        void showClearAllButton(boolean visible);

        void showDatePicker(List<Date> availableDates, Meal meal);

        void showMealTypePicker(Meal meal, Date date);

        void showMessage(String message);

        void navigateBack();

        void navigateToMealDetails(String mealId);

        void updateMeal(Meal meal);
    }

    interface Presenter {
        void search(String query);

        void onFilterClicked(FilterType type);

        void onOptionSelected(String option, FilterType type);

        void onRemoveFilter(FilterType type);

        void onClearAllFilters();

        void onAddToPlanClicked(Meal meal);

        void onDateSelected(Meal meal, Date date);

        void onMealTypeSelected(Meal meal, Date date, MealType type);

        void onFavoriteClicked(Meal meal);

        void onMealClicked(Meal meal);

        void onBackClicked();

        void dispose();
    }
}
