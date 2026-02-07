package com.example.mealplanner.ui.ingredients;

public class IngredientsPresenter implements IngredientsContract.Presenter {
    private IngredientsContract.View mView;

    public IngredientsPresenter(IngredientsContract.View view) {
        this.mView = view;
    }
}
