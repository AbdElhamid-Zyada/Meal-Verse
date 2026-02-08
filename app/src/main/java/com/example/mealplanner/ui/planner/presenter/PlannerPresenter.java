package com.example.mealplanner.ui.planner.presenter;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import com.example.mealplanner.repository.MealRepository;
import com.example.mealplanner.repository.MealRepositoryImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlannerPresenter implements PlannerContract.Presenter {

    private final PlannerContract.View view;
    private final MealRepository repository;
    private Date selectedDate;
    private List<Date> currentWeek;

    public PlannerPresenter(PlannerContract.View view) {
        this.view = view;
        this.repository = MealRepositoryImpl.getInstance();
        this.selectedDate = new Date(); // Default to today
    }

    @Override
    public void onViewResumed() {
        loadWeekCalendar();
        selectDate(selectedDate);
    }

    @Override
    public void loadWeekCalendar() {
        // Generate current week starting from today (or maybe start of week? User
        // request: "Today name ... and arranges the full week after it")
        // Request: "gets today name ... and arranges the full week after it"
        // Interpretation: Start with Today, and show the next 6 days.

        currentWeek = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            currentWeek.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        view.showWeekCalendar(currentWeek, selectedDate);
    }

    @Override
    public void selectDate(Date date) {
        this.selectedDate = date;

        // Update Title
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
        view.showSelectedDateTitle(sdf.format(date));

        // Update Calendar Selection UI (handled by View re-rendering adapter or
        // updating state)
        view.showWeekCalendar(currentWeek, selectedDate);

        // Load Meals
        loadMealsForDate(date);
    }

    private void loadMealsForDate(Date date) {
        Map<MealType, Meal> meals = repository.getMealsForDay(date);

        // Update UI for each slot
        updateMealSlot(MealType.BREAKFAST, meals.get(MealType.BREAKFAST));
        updateMealSlot(MealType.LUNCH, meals.get(MealType.LUNCH));
        updateMealSlot(MealType.DINNER, meals.get(MealType.DINNER));
    }

    private void updateMealSlot(MealType type, Meal meal) {
        if (meal != null) {
            view.showMeal(type, meal);
        } else {
            view.showEmptySlot(type);
        }
    }

    @Override
    public void deleteMeal(MealType type) {
        repository.deleteMeal(selectedDate, type);
        // Refresh
        loadMealsForDate(selectedDate);
    }

    @Override
    public void onMealClicked(Meal meal) {
        view.navigateToMealDetails(meal.getId());
    }
}
