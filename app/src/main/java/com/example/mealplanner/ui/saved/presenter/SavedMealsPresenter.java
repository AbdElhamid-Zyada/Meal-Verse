package com.example.mealplanner.ui.saved.presenter;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.repository.MealRepository;
import com.example.mealplanner.repository.MealRepositoryImpl;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SavedMealsPresenter implements SavedMealsContract.Presenter {

    private SavedMealsContract.View view;
    private MealRepository repository;
    private CompositeDisposable disposables;

    public SavedMealsPresenter(SavedMealsContract.View view) {
        this.view = view;
        this.repository = MealRepositoryImpl.getInstance();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadSavedMeals() {
        view.showLoading();
        disposables.add(repository.getFavoriteMeals()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            view.hideLoading();
                            if (meals == null || meals.isEmpty()) {
                                view.showEmptyState();
                            } else {
                                view.showSavedMeals(meals);
                            }
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showMessage("Error loading meals: " + throwable.getMessage());
                        }));
    }

    @Override
    public void removeMeal(Meal meal) {
        disposables.add(repository.removeFavorite(meal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.showMessage("Meal removed from favorites");
                            // List should update automatically via Observable, but we can verify
                        },
                        throwable -> view.showMessage("Error removing meal: " + throwable.getMessage())));
    }

    @Override
    public void onMealClicked(Meal meal) {
        view.navigateToMealDetails(meal.getId());
    }

    @Override
    public void dispose() {
        if (disposables != null && !disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}
