package com.example.mealplanner.model.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoriesResponse {
    @SerializedName("categories")
    private List<CategoryApiModel> categories;

    public List<CategoryApiModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryApiModel> categories) {
        this.categories = categories;
    }
}
