package com.example.mealplanner.utils;

import com.example.mealplanner.model.Category;
import com.example.mealplanner.model.Country;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.api.AreaApiModel;
import com.example.mealplanner.model.api.CategoryApiModel;
import com.example.mealplanner.model.api.IngredientApiModel;
import com.example.mealplanner.model.api.MealApiModel;

import java.util.ArrayList;
import java.util.List;

public class ApiMapper {

    public static Meal mapMealApiModelToMeal(MealApiModel apiModel) {
        if (apiModel == null)
            return null;

        Meal meal = new Meal();
        meal.setId(apiModel.getIdMeal());
        meal.setName(apiModel.getStrMeal());
        meal.setCategory(apiModel.getStrCategory());
        meal.setArea(apiModel.getStrArea());
        meal.setInstructions(apiModel.getStrInstructions());
        meal.setYoutubeUrl(apiModel.getStrYoutube());
        meal.setSourceUrl(apiModel.getStrSource());

        // Set image URL from API
        meal.setImageUrl(apiModel.getStrMealThumb());

        // Extract ingredients and measures
        List<String> ingredients = extractIngredients(apiModel);
        List<String> measures = extractMeasures(apiModel);

        meal.setIngredients(ingredients);
        meal.setMeasures(measures);

        return meal;
    }

    private static List<String> extractIngredients(MealApiModel apiModel) {
        List<String> ingredients = new ArrayList<>();
        addIfNotEmpty(ingredients, apiModel.getStrIngredient1());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient2());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient3());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient4());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient5());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient6());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient7());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient8());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient9());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient10());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient11());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient12());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient13());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient14());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient15());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient16());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient17());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient18());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient19());
        addIfNotEmpty(ingredients, apiModel.getStrIngredient20());
        return ingredients;
    }

    private static List<String> extractMeasures(MealApiModel apiModel) {
        List<String> measures = new ArrayList<>();
        addIfNotEmpty(measures, apiModel.getStrMeasure1());
        addIfNotEmpty(measures, apiModel.getStrMeasure2());
        addIfNotEmpty(measures, apiModel.getStrMeasure3());
        addIfNotEmpty(measures, apiModel.getStrMeasure4());
        addIfNotEmpty(measures, apiModel.getStrMeasure5());
        addIfNotEmpty(measures, apiModel.getStrMeasure6());
        addIfNotEmpty(measures, apiModel.getStrMeasure7());
        addIfNotEmpty(measures, apiModel.getStrMeasure8());
        addIfNotEmpty(measures, apiModel.getStrMeasure9());
        addIfNotEmpty(measures, apiModel.getStrMeasure10());
        addIfNotEmpty(measures, apiModel.getStrMeasure11());
        addIfNotEmpty(measures, apiModel.getStrMeasure12());
        addIfNotEmpty(measures, apiModel.getStrMeasure13());
        addIfNotEmpty(measures, apiModel.getStrMeasure14());
        addIfNotEmpty(measures, apiModel.getStrMeasure15());
        addIfNotEmpty(measures, apiModel.getStrMeasure16());
        addIfNotEmpty(measures, apiModel.getStrMeasure17());
        addIfNotEmpty(measures, apiModel.getStrMeasure18());
        addIfNotEmpty(measures, apiModel.getStrMeasure19());
        addIfNotEmpty(measures, apiModel.getStrMeasure20());
        return measures;
    }

    private static void addIfNotEmpty(List<String> list, String value) {
        if (value != null && !value.trim().isEmpty()) {
            list.add(value.trim());
        }
    }

    public static Ingredient mapIngredientApiModelToIngredient(IngredientApiModel apiModel) {
        if (apiModel == null)
            return null;
        return new Ingredient(
                apiModel.getIdIngredient(),
                apiModel.getStrIngredient(),
                apiModel.getStrDescription(),
                apiModel.getStrType());
    }

    public static Country mapAreaApiModelToCountry(AreaApiModel apiModel) {
        if (apiModel == null)
            return null;
        String areaName = apiModel.getStrArea();
        String flagUrl = CountryFlagMapper.getFlagUrl(areaName);
        return new Country(areaName, flagUrl);
    }

    public static Category mapCategoryApiModelToCategory(CategoryApiModel apiModel) {
        if (apiModel == null)
            return null;
        return new Category(
                apiModel.getIdCategory(),
                apiModel.getStrCategory(),
                apiModel.getStrCategoryThumb(),
                apiModel.getStrCategoryDescription());
    }
}
