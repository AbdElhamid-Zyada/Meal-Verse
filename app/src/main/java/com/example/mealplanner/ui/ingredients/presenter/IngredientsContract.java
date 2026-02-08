package com.example.mealplanner.ui.ingredients.presenter;

import com.example.mealplanner.model.Ingredient;
import java.util.List;

public interface IngredientsContract {
    interface View {
        void showIngredients(List<Ingredient> ingredients);

        void showLoading();

        void hideLoading();

        void showError(String message);

        void navigateBack();

        void navigateToSearch(String ingredientName);
    }

    interface Presenter {
        void loadIngredients();

        void searchIngredients(String query);

        void onBackClicked();

        void onIngredientClicked(Ingredient ingredient);

        void dispose();
    }
}
