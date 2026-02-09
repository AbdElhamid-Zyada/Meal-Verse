package com.example.mealplanner.datasource.firestore;

import com.example.mealplanner.model.MealOfTheDay;
import com.example.mealplanner.model.MealType;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FirestoreDataSourceImpl implements FirestoreDataSource {
    private static final String COLLECTION_MEAL_OF_THE_DAY = "mealOfTheDay";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_SAVED_MEALS = "savedMeals";
    private static final String COLLECTION_PLANNED_MEALS = "plannedMeals";

    private final FirebaseFirestore firestore;

    public FirestoreDataSourceImpl() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public Completable saveMealOfTheDay(String userId, String mealId, String date) {
        return Completable.create(emitter -> {
            MealOfTheDay mealOfTheDay = new MealOfTheDay(
                    userId,
                    mealId,
                    date,
                    System.currentTimeMillis());

            String documentId = userId + "_" + date;
            firestore.collection(COLLECTION_MEAL_OF_THE_DAY)
                    .document(documentId)
                    .set(mealOfTheDay)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Single<String> getMealOfTheDayId(String userId, String date) {
        return Single.create(emitter -> {
            String documentId = userId + "_" + date;
            firestore.collection(COLLECTION_MEAL_OF_THE_DAY)
                    .document(documentId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            MealOfTheDay mealOfTheDay = documentSnapshot.toObject(MealOfTheDay.class);
                            if (mealOfTheDay != null && mealOfTheDay.getMealId() != null) {
                                emitter.onSuccess(mealOfTheDay.getMealId());
                            } else {
                                emitter.onSuccess(""); // No meal saved
                            }
                        } else {
                            emitter.onSuccess(""); // No meal saved
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable saveFavoriteMeal(String userId, String mealId) {
        return Completable.create(emitter -> {
            Map<String, Object> data = new HashMap<>();
            data.put("saved", true);
            data.put("timestamp", System.currentTimeMillis());

            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_SAVED_MEALS)
                    .document(mealId)
                    .set(data)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable removeFavoriteMeal(String userId, String mealId) {
        return Completable.create(emitter -> {
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_SAVED_MEALS)
                    .document(mealId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Single<List<String>> getFavoriteMealIds(String userId) {
        return Single.create(emitter -> {
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_SAVED_MEALS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> mealIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            mealIds.add(document.getId());
                        }
                        emitter.onSuccess(mealIds);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable savePlannedMeal(String userId, String mealId, Date date, MealType type) {
        return Completable.create(emitter -> {
            PlannedMealFirestore plannedMeal = new PlannedMealFirestore(mealId, date, type);

            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_PLANNED_MEALS)
                    .add(plannedMeal)
                    .addOnSuccessListener(documentReference -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Completable removePlannedMeal(String userId, String mealId, Date date, MealType type) {
        return Completable.create(emitter -> {
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_PLANNED_MEALS)
                    .whereEqualTo("mealId", mealId)
                    .whereEqualTo("date", date)
                    .whereEqualTo("type", type.name())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        emitter.onComplete();
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Single<List<PlannedMealFirestore>> getPlannedMealsForWeek(String userId, Date startDate, Date endDate) {
        return Single.create(emitter -> {
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_PLANNED_MEALS)
                    .whereGreaterThanOrEqualTo("date", startDate)
                    .whereLessThanOrEqualTo("date", endDate)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<PlannedMealFirestore> plannedMeals = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            PlannedMealFirestore plannedMeal = document.toObject(PlannedMealFirestore.class);
                            plannedMeals.add(plannedMeal);
                        }
                        emitter.onSuccess(plannedMeals);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
}
