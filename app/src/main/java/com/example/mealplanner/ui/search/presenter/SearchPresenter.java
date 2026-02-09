package com.example.mealplanner.ui.search.presenter;

import com.example.mealplanner.model.FilterType;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import com.example.mealplanner.repository.MealRepository;
import com.example.mealplanner.repository.MealRepositoryImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

public class SearchPresenter implements SearchContract.Presenter {

    private final SearchContract.View view;
    private final MealRepository repository;
    private final com.example.mealplanner.repository.UserRepository userRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final PublishSubject<String> searchSubject = PublishSubject.create();

    private String currentQuery = "";
    private final Map<FilterType, String> activeFilters = new HashMap<>();

    public SearchPresenter(SearchContract.View view, com.example.mealplanner.repository.UserRepository userRepository) {
        this.view = view;
        this.repository = MealRepositoryImpl.getInstance();
        this.userRepository = userRepository;
        setupDebouncedSearch();
    }

    private void setupDebouncedSearch() {
        disposables.add(
                searchSubject
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                query -> {
                                    this.currentQuery = query;
                                    performSearch();
                                },
                                throwable -> view.showMessage("Search error: " + throwable.getMessage())));
    }

    @Override
    public void init() {
        performSearch();
    }

    @Override
    public void search(String query) {
        // Emit to debounced subject instead of searching immediately
        searchSubject.onNext(query);
    }

    private io.reactivex.rxjava3.disposables.Disposable searchDisposable;

    private void performSearch() {
        String category = activeFilters.get(FilterType.CATEGORY);
        String area = activeFilters.get(FilterType.AREA);
        String ingredient = activeFilters.get(FilterType.INGREDIENT);

        // Fetch both search results and favorites to sync state
        io.reactivex.rxjava3.core.Observable<List<Meal>> searchObservable = repository.searchMeals(currentQuery,
                category, area, ingredient);
        io.reactivex.rxjava3.core.Observable<List<Meal>> favoritesObservable = repository.getFavoriteMeals();

        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }

        searchDisposable = io.reactivex.rxjava3.core.Observable
                .combineLatest(searchObservable, favoritesObservable, (searchResults, favorites) -> {
                    // Create a set of favorite IDs for O(1) lookup
                    java.util.Set<String> favoriteIds = new java.util.HashSet<>();
                    for (Meal fav : favorites) {
                        favoriteIds.add(fav.getId());
                    }

                    // Update isFavorite flag for each search result
                    for (Meal meal : searchResults) {
                        meal.setFavorite(favoriteIds.contains(meal.getId()));
                    }
                    return searchResults;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            view.showSearchResults(meals);
                            view.showResultCount(meals.size());
                        },
                        throwable -> view.showMessage("Error searching: " + throwable.getMessage()));

        disposables.add(searchDisposable);
    }

    @Override
    public void onFilterClicked(FilterType type) {
        disposables.add(repository.getFilterOptions(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        options -> view.showFilterOptions(options, type),
                        throwable -> view.showMessage("Error loading options: " + throwable.getMessage())));
    }

    @Override
    public void onOptionSelected(String option, FilterType type) {
        // Remove existing chip for this type if any, then add new one
        if (activeFilters.containsKey(type)) {
            view.removeFilterChip(type);
        }
        activeFilters.put(type, option);
        view.addFilterChip(option, type);
        view.showClearAllButton(true);
        performSearch();
    }

    @Override
    public void onRemoveFilter(FilterType type) {
        activeFilters.remove(type);
        view.removeFilterChip(type);
        if (activeFilters.isEmpty()) {
            view.showClearAllButton(false);
        }
        performSearch();
    }

    @Override
    public void onClearAllFilters() {
        activeFilters.clear();
        view.clearAllChips();
        view.showClearAllButton(false);
        performSearch();
    }

    @Override
    public void onAddToPlanClicked(Meal meal) {
        disposables.add(userRepository.isGuestMode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isGuest -> {
                            if (isGuest) {
                                view.showMessage("Adding to weekly plan is not available in guest mode");
                                return;
                            }
                            showDatePicker(meal);
                        },
                        error -> view.showMessage("Error checking guest status")));
    }

    private void showDatePicker(Meal meal) {
        // Generate next 7 days dates
        List<Date> availableDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            availableDates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        view.showDatePicker(availableDates, meal);
    }

    @Override
    public void onDateSelected(Meal meal, Date date) {
        view.showMealTypePicker(meal, date);
    }

    @Override
    public void onMealTypeSelected(Meal meal, Date date, MealType type) {
        disposables.add(repository.addMealToPlan(meal, date, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> view.showMessage("Meal added to plan!"),
                        throwable -> view.showMessage("Error adding to plan: " + throwable.getMessage())));
    }

    @Override
    public void onFavoriteClicked(Meal meal) {
        disposables.add(userRepository.isGuestMode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isGuest -> {
                            if (isGuest) {
                                view.showMessage("Adding to favorites is not available in guest mode");
                                return;
                            }
                            toggleFavorite(meal);
                        },
                        error -> view.showMessage("Error checking guest status")));
    }

    private void toggleFavorite(Meal meal) {
        // Toggle state immediately for UI responsiveness
        boolean newStatus = !meal.isFavorite();
        meal.setFavorite(newStatus);

        // Optimistic update
        view.updateMeal(meal);

        // Perform DB operation
        io.reactivex.rxjava3.core.Completable operation;
        if (newStatus) {
            operation = repository.addFavorite(meal);
        } else {
            operation = repository.removeFavorite(meal);
        }

        disposables.add(operation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            // Success, UI already updated
                        },
                        throwable -> {
                            // Revert on failure
                            meal.setFavorite(!newStatus);
                            view.updateMeal(meal);
                            view.showMessage("Error updating favorite: " + throwable.getMessage());
                        }));
    }

    @Override
    public void onMealClicked(Meal meal) {
        view.navigateToMealDetails(meal.getId());
    }

    @Override
    public void onBackClicked() {
        view.navigateBack();
    }

    @Override
    public void dispose() {
        disposables.clear();
    }
}
