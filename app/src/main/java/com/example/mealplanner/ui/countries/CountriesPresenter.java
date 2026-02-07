package com.example.mealplanner.ui.countries;

public class CountriesPresenter implements CountriesContract.Presenter {
    private CountriesContract.View mView;

    public CountriesPresenter(CountriesContract.View view) {
        this.mView = view;
    }
}
