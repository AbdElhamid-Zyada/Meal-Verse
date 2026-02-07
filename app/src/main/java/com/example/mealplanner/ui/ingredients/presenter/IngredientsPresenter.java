package com.example.mealplanner.ui.ingredients.presenter;

public class IngredientsPresenter implements IngredientsContract.Presenter {
    private IngredientsContract.View mView;

    public IngredientsPresenter(IngredientsContract.View view) {
        this.mView = view;
    }
}
