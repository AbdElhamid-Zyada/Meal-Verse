package com.example.mealplanner.ui.search.presenter;

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;

    public SearchPresenter(SearchContract.View view) {
        this.mView = view;
    }
}
