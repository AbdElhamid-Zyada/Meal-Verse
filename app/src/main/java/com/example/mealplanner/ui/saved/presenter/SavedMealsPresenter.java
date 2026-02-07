package com.example.mealplanner.ui.saved.presenter;

public class SavedMealsPresenter implements SavedMealsContract.Presenter {
    private SavedMealsContract.View mView;

    public SavedMealsPresenter(SavedMealsContract.View view) {
        this.mView = view;
    }
}
