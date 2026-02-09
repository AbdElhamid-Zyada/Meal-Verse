package com.example.mealplanner.ui.home.presenter;

import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.model.Country;
import com.example.mealplanner.repository.MealRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View mView;
    private MealRepositoryImpl repository;
    private CompositeDisposable disposables;
    private Meal currentMealOfTheDay;

    public HomePresenter(HomeContract.View view) {
        this.mView = view;
        this.repository = MealRepositoryImpl.getInstance();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void onSearchClicked() {
        mView.navigateToSearch();
    }

    @Override
    public void onMealOfDayClicked() {
        if (currentMealOfTheDay != null) {
            mView.navigateToMealDetails(currentMealOfTheDay.getId());
        }
    }

    @Override
    public void onSeeAllCountriesClicked() {
        mView.navigateToCountries();
    }

    @Override
    public void onBrowseAllIngredientsClicked() {
        mView.navigateToIngredients();
    }

    @Override
    public void onSuggestedMealClicked(Meal meal) {
        if (meal != null) {
            mView.navigateToMealDetails(meal.getId());
        }
    }

    @Override
    public void onViewResumed() {
        // Load data from API
        loadCountries();
        loadIngredients();

        // Load Meal of the Day
        loadMealOfTheDay();

        // Load Suggested Meals
        loadSuggestedMeals();
    }

    @Override
    public void loadMealOfTheDay() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            mView.showMealOfTheDayError("Please login to see Meal of the Day");
            return;
        }

        String userId = currentUser.getUid();
        mView.showLoading();

        // Check if meal of the day exists in Firestore
        disposables.add(
                repository.getMealOfTheDayId(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                mealId -> {
                                    if (mealId != null && !mealId.isEmpty()) {
                                        // Fetch the saved meal
                                        fetchMealById(mealId);
                                    } else {
                                        // Get random meal and save it
                                        fetchRandomMealAndSave(userId);
                                    }
                                },
                                error -> {
                                    mView.hideLoading();
                                    mView.showMealOfTheDayError("Failed to load Meal of the Day");
                                }));
    }

    private void fetchMealById(String mealId) {
        disposables.add(
                repository.getMealDetails(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    mView.hideLoading();
                                    currentMealOfTheDay = meal;
                                    mView.displayMealOfTheDay(meal);
                                },
                                error -> {
                                    mView.hideLoading();
                                    mView.showMealOfTheDayError("Failed to load meal details");
                                }));
    }

    private void fetchRandomMealAndSave(String userId) {
        disposables.add(
                repository.getRandomMeal()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meal -> {
                                    currentMealOfTheDay = meal;
                                    mView.displayMealOfTheDay(meal);
                                    mView.hideLoading();

                                    // Save to Firestore
                                    saveMealOfTheDay(userId, meal.getId());
                                },
                                error -> {
                                    mView.hideLoading();
                                    mView.showMealOfTheDayError("Failed to fetch random meal");
                                }));
    }

    private void saveMealOfTheDay(String userId, String mealId) {
        disposables.add(
                repository.saveMealOfTheDay(userId, mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Successfully saved
                                },
                                error -> {
                                    // Failed to save, but don't show error to user
                                }));
    }

    @Override
    public void onMealOfTheDayCardClicked(Meal meal) {
        // Navigate to meal details with the meal ID
        if (meal != null) {
            mView.navigateToMealDetails(meal.getId());
        }
    }

    @Override
    public void loadSuggestedMeals() {
        // Fetch 2 random meals using RxJava zip
        disposables.add(
                io.reactivex.rxjava3.core.Single.zip(
                        repository.getRandomMeal(),
                        repository.getRandomMeal(),
                        (meal1, meal2) -> {
                            java.util.List<Meal> meals = new java.util.ArrayList<>();
                            meals.add(meal1);
                            meals.add(meal2);
                            return meals;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> mView.displaySuggestedMeals(meals),
                                error -> {
                                    // Silently fail - suggested meals are optional
                                }));
    }

    private void loadCountries() {
        // Fetch first 4 countries from API
        disposables.add(
                repository.getCountries()
                        .map(countries -> {
                            List<Country> selected = new java.util.ArrayList<>();
                            String[] targets = { "Egyptian", "American", "British", "Canadian" };

                            for (String target : targets) {
                                for (Country country : countries) {
                                    if (country.getName().equalsIgnoreCase(target)) {
                                        selected.add(country);
                                        break;
                                    }
                                }
                            }
                            return selected;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                countries -> mView.showCountries(countries),
                                error -> {
                                    // Silently fail - countries are optional
                                }));
    }

    private void loadIngredients() {
        // Fetch first 4 ingredients from API
        disposables.add(
                repository.getIngredients()
                        .map(ingredients -> {
                            // Filter out "Pork" as requested
                            List<Ingredient> filtered = new java.util.ArrayList<>();
                            for (Ingredient ingredient : ingredients) {
                                if (!ingredient.getName().equalsIgnoreCase("Pork")) {
                                    filtered.add(ingredient);
                                }
                            }
                            // Take first 4 ingredients
                            return filtered.size() > 4 ? filtered.subList(0, 4) : filtered;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ingredients -> mView.showIngredients(ingredients),
                                error -> {
                                    // Silently fail - ingredients are optional
                                }));
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        mView = null;
    }
}
