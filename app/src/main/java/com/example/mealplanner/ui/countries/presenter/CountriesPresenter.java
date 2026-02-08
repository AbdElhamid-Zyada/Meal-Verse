package com.example.mealplanner.ui.countries.presenter;

import com.example.mealplanner.model.Country;
import com.example.mealplanner.repository.MealRepository;
import com.example.mealplanner.repository.MealRepositoryImpl;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CountriesPresenter implements CountriesContract.Presenter {

    private CountriesContract.View view;
    private MealRepository repository;
    private CompositeDisposable disposables;

    public CountriesPresenter(CountriesContract.View view) {
        this.view = view;
        this.repository = MealRepositoryImpl.getInstance();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadCountries() {
        view.showLoading();
        disposables.add(repository.getCountries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        countries -> {
                            view.hideLoading();
                            view.showCountries(countries);
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError("Error loading countries: " + throwable.getMessage());
                        }));
    }

    @Override
    public void searchCountries(String query) {
        if (query == null || query.isEmpty()) {
            loadCountries();
            return;
        }

        disposables.add(repository.searchCountries(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        countries -> view.showCountries(countries),
                        throwable -> view.showError("Error searching: " + throwable.getMessage())));
    }

    @Override
    public void onCountryClicked(Country country) {
        view.navigateToSearch(country.getName());
    }

    @Override
    public void onBackClicked() {
        view.navigateBack();
    }

    @Override
    public void dispose() {
        if (disposables != null && !disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}
