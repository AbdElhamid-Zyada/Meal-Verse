package com.example.mealplanner.ui.search;

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;

    public SearchPresenter(SearchContract.View view) {
        this.mView = view;
    }
}
