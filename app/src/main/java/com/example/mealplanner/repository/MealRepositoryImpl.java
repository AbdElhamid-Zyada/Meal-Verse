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

    // Room database for favorites
    private MealDao mealDao;

    private List<Meal> allMockMeals;

    private MealRepositoryImpl(Context context) {
        MealDatabase db = MealDatabase.getInstance(context);
        mealDao = db.mealDao();
        plannedMeals = new HashMap<>();
        initializeMockMeals();
    }

    private void initializeMockMeals() {
        allMockMeals = new java.util.ArrayList<>();

        List<String> ingredients1 = java.util.Arrays.asList("Chicken", "Rice", "Soy Sauce");
        allMockMeals.add(new Meal("1", "Chicken Teriyaki", R.drawable.ic_launcher_background, 520, 25, MealType.LUNCH,
                "Chicken", "Japanese", ingredients1));

        List<String> ingredients2 = java.util.Arrays.asList("Pasta", "Eggs", "Cheese", "Bacon");
        allMockMeals.add(new Meal("2", "Spaghetti Carbonara", R.drawable.ic_launcher_background, 680, 20,
                MealType.DINNER, "Pasta", "Italian", ingredients2));

        List<String> ingredients3 = java.util.Arrays.asList("Beef", "Mushrooms", "Pastry");
        allMockMeals.add(new Meal("3", "Beef Wellington", R.drawable.ic_launcher_background, 800, 60, MealType.DINNER,
                "Beef", "British", ingredients3));

        List<String> ingredients4 = java.util.Arrays.asList("Rice", "Mushrooms", "Cream");
        allMockMeals.add(new Meal("4", "Mushroom Risotto", R.drawable.ic_launcher_background, 450, 40, MealType.DINNER,
                "Fungi", "Italian", ingredients4));

        List<String> ingredients5 = java.util.Arrays.asList("Pork", "Noodles", "Egg", "Broth");
        allMockMeals.add(new Meal("5", "Tonkotsu Ramen", R.drawable.ic_launcher_background, 600, 120, MealType.LUNCH,
                "Pork", "Japanese", ingredients5));

        List<String> ingredients6 = java.util.Arrays.asList("Shrimp", "Curry Paste", "Coconut Milk");
        allMockMeals.add(new Meal("6", "Thai Red Curry", R.drawable.ic_launcher_background, 550, 30, MealType.DINNER,
                "Seafood", "Thai", ingredients6));
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
        if (plannedMeals.containsKey(dateKey)) {
            Map<MealType, Meal> dayMeals = plannedMeals.get(dateKey);
            if (dayMeals != null) {
                dayMeals.remove(type);
            }
        }
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
        return mealDao.insertMeal(meal);
    }

    @Override
    public Completable removeFavorite(Meal meal) {
        return mealDao.deleteMeal(meal);
    }

    @Override
    public Single<Boolean> isFavorite(String mealId) {
        return mealDao.isFavorite(mealId);
    }

    // Ingredients Implementation (Mock)
    @Override
    public Observable<List<Ingredient>> getIngredients() {
        return Observable.fromCallable(this::getMockIngredients);
    }

    @Override
    public Observable<List<Ingredient>> searchIngredients(String query) {
        return Observable.fromCallable(() -> {
            List<Ingredient> all = getMockIngredients();
            List<Ingredient> filtered = new java.util.ArrayList<>();
            for (Ingredient ingredient : all) {
                if (ingredient.getName().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(ingredient);
                }
            }
            return filtered;
        });
    }

    private List<Ingredient> getMockIngredients() {
        List<Ingredient> ingredients = new java.util.ArrayList<>();
        ingredients.add(new Ingredient("Chicken", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Beef", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Salmon", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Lamb", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Pork", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Pasta", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Rice", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Seafood", R.drawable.ic_launcher_background));
        return ingredients;
    }

    // Countries Implementation (Mock)
    @Override
    public Observable<List<Country>> getCountries() {
        return Observable.fromCallable(this::getMockCountries);
    }

    @Override
    public Observable<List<Country>> searchCountries(String query) {
        return Observable.fromCallable(() -> {
            List<Country> all = getMockCountries();
            List<Country> filtered = new java.util.ArrayList<>();
            for (Country country : all) {
                if (country.getName().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(country);
                }
            }
            return filtered;
        });
    }

    private List<Country> getMockCountries() {
        List<Country> countries = new java.util.ArrayList<>();
        countries.add(new Country("Italian", R.drawable.ic_launcher_background));
        countries.add(new Country("Mexican", R.drawable.ic_launcher_background));
        countries.add(new Country("Japanese", R.drawable.ic_launcher_background));
        countries.add(new Country("American", R.drawable.ic_launcher_background));
        countries.add(new Country("French", R.drawable.ic_launcher_background));
        countries.add(new Country("British", R.drawable.ic_launcher_background));
        countries.add(new Country("Chinese", R.drawable.ic_launcher_background));
        countries.add(new Country("Thai", R.drawable.ic_launcher_background));
        return countries;
    }

    // Search & Filter Implementation
    @Override
    public Observable<List<Meal>> searchMeals(String query, String category, String area, String ingredient) {
        return Observable.fromCallable(() -> {
            List<Meal> filtered = new java.util.ArrayList<>();
            for (Meal meal : allMockMeals) {
                boolean matchesQuery = query == null || query.isEmpty() ||
                        meal.getName().toLowerCase().contains(query.toLowerCase()) ||
                        (meal.getIngredients() != null && meal.getIngredients().stream()
                                .anyMatch(i -> i.toLowerCase().contains(query.toLowerCase())));

                boolean matchesCategory = category == null || category.isEmpty()
                        || meal.getCategory().equalsIgnoreCase(category);
                boolean matchesArea = area == null || area.isEmpty() || meal.getArea().equalsIgnoreCase(area);
                boolean matchesIngredient = ingredient == null || ingredient.isEmpty() ||
                        (meal.getIngredients() != null && meal.getIngredients().contains(ingredient));

                if (matchesQuery && matchesCategory && matchesArea && matchesIngredient) {
                    filtered.add(meal);
                }
            }
            return filtered;
        });
    }

    @Override
    public Observable<List<String>> getFilterOptions(FilterType type) {
        return Observable.fromCallable(() -> {
            java.util.Set<String> options = new java.util.HashSet<>();
            for (Meal meal : allMockMeals) {
                switch (type) {
                    case CATEGORY:
                        if (meal.getCategory() != null)
                            options.add(meal.getCategory());
                        break;
                    case AREA:
                        if (meal.getArea() != null)
                            options.add(meal.getArea());
                        break;
                    case INGREDIENT:
                        if (meal.getIngredients() != null)
                            options.addAll(meal.getIngredients());
                        break;
                }
            }
            return new java.util.ArrayList<>(options);
        });
    }

    @Override
    public Completable addMealToPlan(Meal meal, Date date, MealType type) {
        return Completable.fromAction(() -> {
            String dateKey = getDateKey(date);
            if (!plannedMeals.containsKey(dateKey)) {
                plannedMeals.put(dateKey, new HashMap<>());
            }
            plannedMeals.get(dateKey).put(type, meal);
        });
    }
}
