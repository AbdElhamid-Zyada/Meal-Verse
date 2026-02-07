package com.example.mealplanner.ui.home.presenter;

import com.example.mealplanner.R;
import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View mView;

    public HomePresenter(HomeContract.View view) {
        this.mView = view;
    }

    @Override
    public void onSearchClicked() {
        mView.navigateToSearch();
    }

    @Override
    public void onMealOfDayClicked() {
        mView.navigateToMealDetails();
    }

    @Override
    public void onSeeAllCountriesClicked() {
        mView.navigateToCountries();
    }

    @Override
    public void onBrowseAllIngredientsClicked() {
        mView.navigateToIngredients();
    }

    @Override
    public void onSuggestedMealClicked() {
        mView.navigateToMealDetails();
    }

    @Override
    public void onViewResumed() {
        mView.showCountries(getMockCountries());
        mView.showIngredients(getMockIngredients());
    }

    private List<Country> getMockCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Italian", R.drawable.ic_launcher_background));
        countries.add(new Country("Mexican", R.drawable.ic_launcher_background));
        countries.add(new Country("Japanese", R.drawable.ic_launcher_background));
        countries.add(new Country("Indian", R.drawable.ic_launcher_background));
        return countries;
    }

    private List<Ingredient> getMockIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Chicken", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Beef", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Seafood", R.drawable.ic_launcher_background));
        ingredients.add(new Ingredient("Veggie", R.drawable.ic_launcher_background));
        return ingredients;
    }
}
