package com.example.mealplanner.ui.planner.presenter;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PlannerContract {
    interface View {
        void showWeekCalendar(List<Date> days, Date selectedDate);

        void showSelectedDateTitle(String title); // e.g., "Monday, Oct 23"

        void showMeals(Map<MealType, Meal> meals);

        void showMeal(MealType type, Meal meal);

        void showEmptySlot(MealType type);

        void showMessage(String message);

        void navigateToMealDetails(String mealId);
    }

    interface Presenter {
        void loadWeekCalendar();

        void selectDate(Date date);

        void deleteMeal(MealType type);

        void onMealClicked(Meal meal);

        void onViewResumed();
    }
}
