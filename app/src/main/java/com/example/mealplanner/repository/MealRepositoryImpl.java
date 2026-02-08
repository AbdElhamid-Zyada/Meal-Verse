package com.example.mealplanner.repository;

import android.content.Context;
import com.example.mealplanner.R;
import com.example.mealplanner.db.MealDao;
import com.example.mealplanner.db.MealDatabase;
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

    private MealRepositoryImpl(Context context) {
        MealDatabase db = MealDatabase.getInstance(context);
        mealDao = db.mealDao();
        plannedMeals = new HashMap<>();
        seedData();
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

    private void seedData() {
        // Simplified: Just putting meals for "today" effectively for the Planner mock
        // part
    }

    @Override
    public Map<MealType, Meal> getMealsForDay(Date date) {
        String dateKey = getDateKey(date);

        if (!plannedMeals.containsKey(dateKey)) {
            // Seed data for this day if not exists (for demo purposes)
            Map<MealType, Meal> dayMeals = new HashMap<>();
            dayMeals.put(MealType.BREAKFAST,
                    new Meal("1", "Pancakes", R.drawable.ic_launcher_background, 350, 15, MealType.BREAKFAST));
            dayMeals.put(MealType.LUNCH,
                    new Meal("2", "Chicken Teriyaki", R.drawable.ic_launcher_background, 520, 25, MealType.LUNCH));
            dayMeals.put(MealType.DINNER,
                    new Meal("3", "Spaghetti Carbonara", R.drawable.ic_launcher_background, 680, 20, MealType.DINNER));
            plannedMeals.put(dateKey, dayMeals);
        }

        return new HashMap<>(plannedMeals.get(dateKey));
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
}
