package com.example.mealplanner.ui.countries.presenter;

import com.example.mealplanner.model.Country;
import java.util.List;

public interface CountriesContract {
    interface View {
        void showCountries(List<Country> countries);

        void showLoading();

        void hideLoading();

        void showError(String message);

        void navigateBack();
    }

    interface Presenter {
        void loadCountries();

        void searchCountries(String query);

        void onBackClicked();

        void dispose();
    }
}
