package com.example.mealplanner.model.api;

import com.google.gson.annotations.SerializedName;

public class AreaApiModel {
    @SerializedName("strArea")
    private String strArea;

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }
}
