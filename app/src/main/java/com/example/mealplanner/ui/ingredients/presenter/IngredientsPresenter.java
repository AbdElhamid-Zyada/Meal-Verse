package com.example.mealplanner.ui.ingredients.presenter;

import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.repository.MealRepository;
import com.example.mealplanner.repository.MealRepositoryImpl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class IngredientsPresenter implements IngredientsContract.Presenter {

    private IngredientsContract.View view;
    private MealRepository repository;
    private CompositeDisposable disposables;

    public IngredientsPresenter(IngredientsContract.View view) {
        this.view = view;
        this.repository = MealRepositoryImpl.getInstance();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadIngredients() {
        view.showLoading();
        disposables.add(repository.getIngredients()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ingredients -> {
                            view.hideLoading();
                            view.showIngredients(ingredients);
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError("Error loading ingredients: " + throwable.getMessage());
                        }));
    }

    @Override
    public void searchIngredients(String query) {
        // Simple debounce could be added here if this was triggered on text change
        // aggressively,
        // but for now, we just call the repo which is mocked.
        // If query is empty, load all.
        if (query == null || query.isEmpty()) {
            loadIngredients();
            return;
        }

        disposables.add(repository.searchIngredients(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ingredients -> view.showIngredients(ingredients),
                        throwable -> view.showError("Error searching: " + throwable.getMessage())));
    }

    @Override
    public void onBackClicked() {
        view.navigateBack();
    }

    @Override
    public void dispose() {
        if (disposables != null && !disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}
