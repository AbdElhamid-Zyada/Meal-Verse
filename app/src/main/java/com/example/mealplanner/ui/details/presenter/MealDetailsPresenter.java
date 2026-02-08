package com.example.mealplanner.ui.details.presenter;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import com.example.mealplanner.repository.MealRepository;
import com.example.mealplanner.repository.MealRepositoryImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {

    private final MealDetailsContract.View view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Meal currentMeal;

    public MealDetailsPresenter(MealDetailsContract.View view) {
        this.view = view;
        this.repository = MealRepositoryImpl.getInstance();
    }

    @Override
    public void loadMealDetails(String mealId) {
        disposables.add(repository.getMealDetails(mealId)
                .flatMap(meal -> {
                    // Check favorite state
                    return repository.isFavorite(meal.getId())
                            .map(isFav -> {
                                meal.setFavorite(isFav);
                                return meal;
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onMealLoaded,
                        throwable -> {
                            view.showMessage("Error loading details: " + throwable.getMessage());
                        }));
    }

    private void onMealLoaded(Meal meal) {
        this.currentMeal = meal;
        view.showMealDetails(meal);
        view.setFavoriteState(meal.isFavorite());
        view.showOfflineBadge(meal.isFavorite());

        // Parse Ingredients
        view.showIngredients(meal.getIngredients(), meal.getMeasures());

        // Parse Instructions
        if (meal.getInstructions() != null && !meal.getInstructions().isEmpty()) {
            // Split by period but handle cases like "approx. 5 mins" carefully? User said
            // split by "."
            // Simple split for now as requested
            String[] steps = meal.getInstructions().split("\\.");
            List<String> validSteps = new ArrayList<>();
            for (String step : steps) {
                if (!step.trim().isEmpty()) {
                    validSteps.add(step.trim());
                }
            }
            view.showInstructions(validSteps);
        }

        // Parse Video
        if (meal.getYoutubeUrl() != null) {
            String videoId = extractVideoId(meal.getYoutubeUrl());
            if (videoId != null) {
                view.showVideo(videoId);
            }
        }
    }

    private String extractVideoId(String url) {
        String videoId = null;
        String regex = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+";
        if (url != null && url.matches(regex)) {
            if (url.contains("v=")) {
                int index = url.indexOf("v=");
                int endIndex = url.indexOf("&", index);
                if (endIndex == -1) {
                    videoId = url.substring(index + 2);
                } else {
                    videoId = url.substring(index + 2, endIndex);
                }
            } else if (url.contains("youtu.be/")) {
                int index = url.indexOf("youtu.be/");
                videoId = url.substring(index + 9);
            }
        }
        return videoId;
    }

    @Override
    public void onFavoriteClicked() {
        if (currentMeal == null) {
            return;
        }

        boolean newStatus = !currentMeal.isFavorite();
        currentMeal.setFavorite(newStatus);
        view.setFavoriteState(newStatus);
        view.showOfflineBadge(newStatus);

        io.reactivex.rxjava3.core.Completable operation;
        if (newStatus) {
            operation = repository.addFavorite(currentMeal);
        } else {
            operation = repository.removeFavorite(currentMeal);
        }

        disposables.add(operation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.showMessage(newStatus ? "Added to favorites!" : "Removed from favorites!");
                        }, // Success
                        throwable -> {
                            // Revert on error
                            currentMeal.setFavorite(!newStatus);
                            view.setFavoriteState(!newStatus);
                            view.showOfflineBadge(!newStatus);
                            view.showMessage("Error updating favorite: " + throwable.getMessage());
                        }));
    }

    @Override
    public void onAddToPlanClicked() {
        if (currentMeal == null)
            return;

        List<Date> availableDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            availableDates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        view.showDatePicker(availableDates, currentMeal);
    }

    @Override
    public void onDateSelected(Date date) {
        if (currentMeal == null)
            return;
        view.showMealTypePicker(currentMeal, date);
    }

    @Override
    public void onMealTypeSelected(Date date, MealType type) {
        if (currentMeal == null)
            return;
        disposables.add(repository.addMealToPlan(currentMeal, date, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> view.showMessage("Meal added to plan!"),
                        throwable -> view.showMessage("Error adding to plan: " + throwable.getMessage())));
    }

    @Override
    public void dispose() {
        disposables.clear();
    }
}
