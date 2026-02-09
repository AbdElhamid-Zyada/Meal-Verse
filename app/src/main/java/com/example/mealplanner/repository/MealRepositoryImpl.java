package com.example.mealplanner.repository;

import android.content.Context;
import com.example.mealplanner.R;
import com.example.mealplanner.db.MealDao;
import com.example.mealplanner.db.MealDatabase;
import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.FilterType;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class MealRepositoryImpl implements MealRepository {

    private static MealRepositoryImpl instance;

    // Mock database for planner: Map<DateString, Map<MealType, Meal>>
    private Map<String, Map<MealType, Meal>> plannedMeals;

    // Room database for favorites and planned meals
    private MealDao mealDao;
    private com.example.mealplanner.db.PlannedMealDao plannedMealDao;

    // API data sources
    private com.example.mealplanner.datasource.remote.MealRemoteDataSource remoteDataSource;
    private com.example.mealplanner.datasource.firestore.FirestoreDataSource firestoreDataSource;

    // Disposables for repository internal subscriptions
    private final io.reactivex.rxjava3.disposables.CompositeDisposable disposables = new io.reactivex.rxjava3.disposables.CompositeDisposable();

    private MealRepositoryImpl(Context context) {
        MealDatabase db = MealDatabase.getInstance(context);
        mealDao = db.mealDao();
        plannedMealDao = db.plannedMealDao();
        plannedMeals = new HashMap<>();

        // Initialize API data sources
        remoteDataSource = new com.example.mealplanner.datasource.remote.MealRemoteDataSourceImpl();
        firestoreDataSource = new com.example.mealplanner.datasource.firestore.FirestoreDataSourceImpl();

        loadPlannedMealsFromDb();
    }

    private void loadPlannedMealsFromDb() {
        disposables.add(plannedMealDao.getAllPlannedMeals()
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(
                        storedPlans -> {
                            for (com.example.mealplanner.model.PlannedMeal plan : storedPlans) {
                                // Find the meal object by ID
                                Meal meal = findMealById(plan.getMealId());
                                if (meal != null) {
                                    // Update cache
                                    // Note: Using same logic as addMealToPlan but skipping DB insert
                                    String dateKey = getDateKey(plan.getDate());
                                    if (!plannedMeals.containsKey(dateKey)) {
                                        plannedMeals.put(dateKey, new HashMap<>());
                                    }
                                    plannedMeals.get(dateKey).put(plan.getType(), meal);
                                }
                            }
                        },
                        throwable -> {
                            // Log error or handle
                            System.err.println("Error loading planned meals: " + throwable.getMessage());
                        }));
    }

    private Meal findMealById(String id) {
        // Try to find in favorites database
        try {
            List<Meal> favorites = mealDao.getAllMeals().blockingFirst();
            for (Meal meal : favorites) {
                if (meal.getId().equals(id)) {
                    return meal;
                }
            }
        } catch (Exception e) {
            // If not found in favorites, could fetch from API
            System.err.println("Error finding meal: " + e.getMessage());
        }
        return null;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new MealRepositoryImpl(context);
        }
    }

    public static MealRepositoryImpl getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MealRepositoryImpl must be initialized with context first");
        }
        return instance;
    }

    @Override
    public Map<MealType, Meal> getMealsForDay(Date date) {
        String dateKey = getDateKey(date);
        return new HashMap<>(plannedMeals.getOrDefault(dateKey, new HashMap<>()));
    }

    @Override
    public void deleteMeal(Date date, MealType type) {
        String dateKey = getDateKey(date);
        Meal mealToDelete = null;

        if (plannedMeals.containsKey(dateKey)) {
            Map<MealType, Meal> dayMeals = plannedMeals.get(dateKey);
            if (dayMeals != null) {
                mealToDelete = dayMeals.get(type);
                dayMeals.remove(type);
            }
        }

        final Meal finalMealToDelete = mealToDelete;

        // Remove from DB
        disposables.add(plannedMealDao.deletePlannedMeal(date, type)
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .andThen(Completable.defer(() -> {
                    // Sync to Firestore if user is authenticated
                    com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth
                            .getInstance().getCurrentUser();
                    if (currentUser != null && finalMealToDelete != null) {
                        return firestoreDataSource
                                .removePlannedMeal(currentUser.getUid(), finalMealToDelete.getId(), date, type)
                                .onErrorComplete(); // Don't fail local operation if Firestore fails
                    }
                    return Completable.complete();
                }))
                .subscribe(
                        () -> {
                        },
                        throwable -> System.err
                                .println("Error deleting planned meal from DB: " + throwable.getMessage())));
    }

    private String getDateKey(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        return sdf.format(date);
    }

    // RxJava Implementations
    @Override
    public Observable<List<Meal>> getFavoriteMeals() {
        return mealDao.getAllMeals();
    }

    @Override
    public Completable addFavorite(Meal meal) {
        return mealDao.insertMeal(meal)
                .andThen(Completable.defer(() -> {
                    // Sync to Firestore if user is authenticated
                    com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth
                            .getInstance().getCurrentUser();
                    if (currentUser != null) {
                        return firestoreDataSource.saveFavoriteMeal(currentUser.getUid(), meal.getId())
                                .onErrorComplete(); // Don't fail local operation if Firestore fails
                    }
                    return Completable.complete();
                }));
    }

    @Override
    public Completable removeFavorite(Meal meal) {
        return mealDao.deleteMeal(meal)
                .andThen(Completable.defer(() -> {
                    // Sync to Firestore if user is authenticated
                    com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth
                            .getInstance().getCurrentUser();
                    if (currentUser != null) {
                        return firestoreDataSource.removeFavoriteMeal(currentUser.getUid(), meal.getId())
                                .onErrorComplete(); // Don't fail local operation if Firestore fails
                    }
                    return Completable.complete();
                }));
    }

    @Override
    public Single<Boolean> isFavorite(String mealId) {
        return mealDao.isFavorite(mealId);
    }

    // Ingredients Implementation (API)
    @Override
    public Observable<List<Ingredient>> getIngredients() {
        return remoteDataSource.getAllIngredients().toObservable();
    }

    @Override
    public Observable<List<Ingredient>> searchIngredients(String query) {
        return remoteDataSource.getAllIngredients()
                .toObservable()
                .map(ingredients -> {
                    List<Ingredient> filtered = new java.util.ArrayList<>();
                    for (Ingredient ingredient : ingredients) {
                        if (ingredient.getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(ingredient);
                        }
                    }
                    return filtered;
                });
    }

    // Countries Implementation (API)
    @Override
    public Observable<List<Country>> getCountries() {
        return remoteDataSource.getAllCountries().toObservable();
    }

    @Override
    public Observable<List<Country>> searchCountries(String query) {
        return remoteDataSource.getAllCountries()
                .toObservable()
                .map(countries -> {
                    List<Country> filtered = new java.util.ArrayList<>();
                    for (Country country : countries) {
                        if (country.getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(country);
                        }
                    }
                    return filtered;
                });
    }

    @Override
    public Observable<List<Meal>> searchMeals(String query, String category, String area, String ingredient) {
        if (query != null && !query.isEmpty()) {
            // Case 1: Search by Name (Returns Full Details)
            return remoteDataSource.searchMealsByName(query)
                    .toObservable()
                    .map(meals -> {
                        List<Meal> filtered = new java.util.ArrayList<>();
                        for (Meal meal : meals) {
                            boolean matchesCategory = category == null || category.isEmpty()
                                    || (meal.getCategory() != null && meal.getCategory().equalsIgnoreCase(category));
                            boolean matchesArea = area == null || area.isEmpty()
                                    || (meal.getArea() != null && meal.getArea().equalsIgnoreCase(area));
                            boolean matchesIngredient = ingredient == null || ingredient.isEmpty()
                                    || (meal.getIngredients() != null && meal.getIngredients().contains(ingredient));

                            if (matchesCategory && matchesArea && matchesIngredient) {
                                filtered.add(meal);
                            }
                        }
                        return filtered;
                    });
        }

        // Case 2: Filter-based Search (Returns Previews - Requires Intersection)
        List<Single<List<Meal>>> sources = new java.util.ArrayList<>();
        if (category != null && !category.isEmpty()) {
            sources.add(remoteDataSource.filterByCategory(category)
                    .map(meals -> {
                        for (Meal meal : meals) {
                            meal.setCategory(category);
                        }
                        return meals;
                    }));
        }
        if (area != null && !area.isEmpty()) {
            sources.add(remoteDataSource.filterByArea(area)
                    .map(meals -> {
                        for (Meal meal : meals) {
                            meal.setArea(area);
                        }
                        return meals;
                    }));
        }
        if (ingredient != null && !ingredient.isEmpty()) {
            sources.add(remoteDataSource.filterByIngredient(ingredient));
        }

        if (sources.isEmpty()) {
            // Case 3: No Filters, No Query -> Default Search (Returns valid meals with Full
            // Details)
            // Iterate 'a' through 'z' to get maximum meals (API limit workaround)
            // This is heavy (~26 calls) but ensures "All Meals" are shown.
            return io.reactivex.rxjava3.core.Observable.range('a', 26)
                    .map(c -> String.valueOf((char) c.intValue()))
                    .flatMap(letter -> remoteDataSource.searchMealsByFirstLetter(letter)
                            .toObservable()
                            .onErrorResumeNext(
                                    t -> io.reactivex.rxjava3.core.Observable.just(new java.util.ArrayList<Meal>())))
                    .reduce((List<Meal>) new java.util.ArrayList<Meal>(), (allMeals, letterMeals) -> {
                        allMeals.addAll(letterMeals);
                        return allMeals;
                    })
                    .toObservable();
        }

        // Intersect results from multiple filters
        return Single.zip(sources, args -> {
            // Start with the first list
            List<Meal> result = (List<Meal>) args[0];

            // Intersect with remaining lists
            for (int i = 1; i < args.length; i++) {
                List<Meal> nextList = (List<Meal>) args[i];
                // Create set of IDs from nextList for fast lookup
                java.util.Set<String> nextIds = new java.util.HashSet<>();
                for (Meal m : nextList) {
                    nextIds.add(m.getId());
                }

                // Keep only meals in result that are also in nextIds
                List<Meal> intersection = new java.util.ArrayList<>();
                for (Meal m : result) {
                    if (nextIds.contains(m.getId())) {
                        intersection.add(m);
                    }
                }
                result = intersection;
            }
            return result;
        })
                .flatMap(meals -> {
                    if (meals.isEmpty())
                        return Single.just(new java.util.ArrayList<Meal>());
                    return Observable.fromIterable(meals)
                            .flatMapSingle(m -> remoteDataSource.getMealById(m.getId()))
                            .toList();
                })
                .toObservable();
    }

    @Override
    public Observable<List<String>> getFilterOptions(FilterType type) {
        switch (type) {
            case CATEGORY:
                return remoteDataSource.getCategories()
                        .toObservable()
                        .map(categories -> {
                            List<String> names = new java.util.ArrayList<>();
                            for (com.example.mealplanner.model.Category category : categories) {
                                names.add(category.getName());
                            }
                            return names;
                        });
            case AREA:
                return remoteDataSource.getAllCountries()
                        .toObservable()
                        .map(countries -> {
                            List<String> names = new java.util.ArrayList<>();
                            for (com.example.mealplanner.model.Country country : countries) {
                                names.add(country.getName());
                            }
                            return names;
                        });
            case INGREDIENT:
                return remoteDataSource.getAllIngredients()
                        .toObservable()
                        .map(ingredients -> {
                            List<String> names = new java.util.ArrayList<>();
                            for (com.example.mealplanner.model.Ingredient ingredient : ingredients) {
                                names.add(ingredient.getName());
                            }
                            return names;
                        });
            default:
                return Observable.just(new java.util.ArrayList<>());
        }
    }

    @Override
    public Completable addMealToPlan(Meal meal, Date date, MealType type) {
        return Completable.fromAction(() -> {
            // Update Cache
            String dateKey = getDateKey(date);
            if (!plannedMeals.containsKey(dateKey)) {
                plannedMeals.put(dateKey, new HashMap<>());
            }
            plannedMeals.get(dateKey).put(type, meal);
        }).andThen(
                // Update DB
                plannedMealDao
                        .insertPlannedMeal(new com.example.mealplanner.model.PlannedMeal(meal.getId(), date, type)))
                .andThen(Completable.defer(() -> {
                    // Sync to Firestore if user is authenticated
                    com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth
                            .getInstance().getCurrentUser();
                    if (currentUser != null) {
                        return firestoreDataSource.savePlannedMeal(currentUser.getUid(), meal.getId(), date, type)
                                .onErrorComplete(); // Don't fail local operation if Firestore fails
                    }
                    return Completable.complete();
                }));
    }

    @Override
    public Single<Meal> getMealDetails(String mealId) {
        System.out.println("[OFFLINE] Attempting to load meal: " + mealId);

        // Try local database first (offline-first pattern)
        return mealDao.getMealById(mealId)
                .doOnSuccess(meal -> System.out.println("[OFFLINE] ✅ Loaded from Room: " + meal.getName()))
                .onErrorResumeNext(error -> {
                    // If not in local DB, try API
                    System.out.println("[OFFLINE] Not in Room, fetching from API");
                    return remoteDataSource.getMealById(mealId)
                            .doOnSuccess(meal -> {
                                System.out.println("[OFFLINE] ✅ Loaded from API: " + meal.getName());
                                // Save to Room for future offline access
                                mealDao.insertMeal(meal)
                                        .subscribe(
                                                () -> System.out.println("[OFFLINE] Cached meal for offline use"),
                                                cacheError -> System.err.println(
                                                        "[OFFLINE] Failed to cache: " + cacheError.getMessage()));
                            })
                            .doOnError(apiError -> System.err
                                    .println("[OFFLINE ERROR] ❌ API fetch failed: " + apiError.getMessage()));
                });
    }

    // New API methods
    public Single<Meal> getRandomMeal() {
        return remoteDataSource.getRandomMeal();
    }

    public Single<List<Meal>> searchMealsByName(String name) {
        return remoteDataSource.searchMealsByName(name);
    }

    public Single<List<com.example.mealplanner.model.Category>> getCategories() {
        return remoteDataSource.getCategories();
    }

    public Completable saveMealOfTheDay(String userId, String mealId) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        String date = sdf.format(new Date());
        return firestoreDataSource.saveMealOfTheDay(userId, mealId, date);
    }

    public Single<String> getMealOfTheDayId(String userId) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        String date = sdf.format(new Date());
        return firestoreDataSource.getMealOfTheDayId(userId, date);
    }

    @Override
    public Completable syncSavedMealsFromFirestore(String userId) {
        System.out.println("[SYNC] Starting syncSavedMealsFromFirestore for user: " + userId);
        return firestoreDataSource.getFavoriteMealIds(userId)
                .doOnSuccess(mealIds -> System.out
                        .println("[SYNC] Retrieved " + mealIds.size() + " saved meal IDs from Firestore"))
                .doOnError(error -> System.err.println("[SYNC ERROR] Failed to get meal IDs: " + error.getMessage()))
                .flatMapCompletable(mealIds -> {
                    if (mealIds.isEmpty()) {
                        System.out.println("[SYNC] No saved meals to sync");
                        return Completable.complete();
                    }

                    System.out.println("[SYNC] Fetching details for " + mealIds.size() + " meals from API");
                    // Fetch full details for each meal ID from API and save to Room
                    return Observable.fromIterable(mealIds)
                            .doOnNext(mealId -> System.out.println("[SYNC] Fetching meal: " + mealId))
                            .flatMapSingle(mealId -> remoteDataSource.getMealById(mealId)
                                    .doOnSuccess(meal -> System.out.println("[SYNC] Fetched meal: " + meal.getName()))
                                    .doOnError(error -> System.err.println(
                                            "[SYNC ERROR] Failed to fetch meal " + mealId + ": " + error.getMessage()))
                                    .onErrorReturnItem(new Meal()) // Skip meals that fail to load
                    )
                            .filter(meal -> meal.getId() != null && !meal.getId().isEmpty())
                            .flatMapCompletable(meal -> {
                                System.out.println("[SYNC] Saving meal to Room: " + meal.getName());
                                return mealDao.insertMeal(meal)
                                        .doOnComplete(() -> System.out
                                                .println("[SYNC] Successfully saved: " + meal.getName()))
                                        .doOnError(error -> System.err
                                                .println("[SYNC ERROR] Failed to save meal: " + error.getMessage()));
                            });
                })
                .doOnComplete(() -> System.out.println("[SYNC] syncSavedMealsFromFirestore completed successfully"))
                .doOnError(error -> System.err
                        .println("[SYNC ERROR] syncSavedMealsFromFirestore failed: " + error.getMessage()));
    }

    @Override
    public Completable syncPlannedMealsFromFirestore(String userId) {
        System.out.println("[SYNC] Starting syncPlannedMealsFromFirestore for user: " + userId);
        // Calculate date range: today + next 6 days
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        Date startDate = calendar.getTime();
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 6);
        Date endDate = calendar.getTime();

        System.out.println("[SYNC] Date range: " + startDate + " to " + endDate);
        return firestoreDataSource.getPlannedMealsForWeek(userId, startDate, endDate)
                .doOnSuccess(meals -> System.out
                        .println("[SYNC] Retrieved " + meals.size() + " planned meals from Firestore"))
                .doOnError(
                        error -> System.err.println("[SYNC ERROR] Failed to get planned meals: " + error.getMessage()))
                .flatMapCompletable(plannedMealsFirestore -> {
                    if (plannedMealsFirestore.isEmpty()) {
                        System.out.println("[SYNC] No planned meals to sync");
                        return Completable.complete();
                    }

                    System.out.println(
                            "[SYNC] Fetching details for " + plannedMealsFirestore.size() + " planned meals from API");
                    // Fetch full details for each meal and save to Room
                    return Observable.fromIterable(plannedMealsFirestore)
                            .doOnNext(pm -> System.out.println(
                                    "[SYNC] Fetching planned meal: " + pm.getMealId() + " for " + pm.getDate()))
                            .flatMapSingle(plannedMeal -> remoteDataSource.getMealById(plannedMeal.getMealId())
                                    .doOnSuccess(meal -> System.out
                                            .println("[SYNC] Fetched planned meal: " + meal.getName()))
                                    .doOnError(error -> System.err.println("[SYNC ERROR] Failed to fetch planned meal "
                                            + plannedMeal.getMealId() + ": " + error.getMessage()))
                                    .map(meal -> {
                                        MealPlannedPair result = new MealPlannedPair();
                                        result.meal = meal;
                                        result.plannedMeal = plannedMeal;
                                        return result;
                                    })
                                    .onErrorReturnItem(new MealPlannedPair()))
                            .filter(pair -> pair.meal != null && pair.meal.getId() != null
                                    && !pair.meal.getId().isEmpty())
                            .flatMapCompletable(pair -> {
                                System.out.println("[SYNC] Saving planned meal to Room: " + pair.meal.getName());

                                // Update in-memory cache
                                String dateKey = getDateKey(pair.plannedMeal.getDate());
                                if (!plannedMeals.containsKey(dateKey)) {
                                    plannedMeals.put(dateKey, new HashMap<>());
                                }
                                plannedMeals.get(dateKey).put(pair.plannedMeal.getType(), pair.meal);
                                System.out.println("[SYNC] Updated in-memory cache for date: " + dateKey + ", type: "
                                        + pair.plannedMeal.getType());

                                // Save meal to favorites (if not already)
                                return mealDao.insertMeal(pair.meal)
                                        .doOnComplete(
                                                () -> System.out.println("[SYNC] Saved meal: " + pair.meal.getName()))
                                        .andThen(plannedMealDao.insertPlannedMeal(
                                                new com.example.mealplanner.model.PlannedMeal(
                                                        pair.meal.getId(),
                                                        pair.plannedMeal.getDate(),
                                                        pair.plannedMeal.getType())))
                                        .doOnComplete(() -> System.out.println("[SYNC] Saved planned meal entry"))
                                        .doOnError(error -> System.err.println(
                                                "[SYNC ERROR] Failed to save planned meal: " + error.getMessage()));
                            });
                })
                .doOnComplete(() -> System.out.println("[SYNC] syncPlannedMealsFromFirestore completed successfully"))
                .doOnError(error -> System.err
                        .println("[SYNC ERROR] syncPlannedMealsFromFirestore failed: " + error.getMessage()));
    }

    @Override
    public Completable clearAllUserData() {
        // Clear in-memory cache
        plannedMeals.clear();

        // Clear Room database
        return mealDao.deleteAllMeals()
                .andThen(plannedMealDao.deleteAllPlannedMeals())
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io());
    }
}

// Helper class for pairing Meal with PlannedMealFirestore
class MealPlannedPair {
    Meal meal;
    com.example.mealplanner.datasource.firestore.PlannedMealFirestore plannedMeal;
}
