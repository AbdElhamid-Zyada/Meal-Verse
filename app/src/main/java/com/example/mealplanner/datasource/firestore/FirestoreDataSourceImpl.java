package com.example.mealplanner.datasource.firestore;

import com.example.mealplanner.model.MealOfTheDay;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class FirestoreDataSourceImpl implements FirestoreDataSource {
    private static final String COLLECTION_MEAL_OF_THE_DAY = "mealOfTheDay";
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
}
