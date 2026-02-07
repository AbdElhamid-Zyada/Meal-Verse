package com.example.mealplanner.ui.meal_details.presenter;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {
    private MealDetailsContract.View mView;

    public MealDetailsPresenter(MealDetailsContract.View view) {
        this.mView = view;
    }
}
