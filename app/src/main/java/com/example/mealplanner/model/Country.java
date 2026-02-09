package com.example.mealplanner.model;

public class Country {
    private String name;
    private String flagUrl; // For API data

    public Country(String name, String flagUrl) {
        this.name = name;
        this.flagUrl = flagUrl;
    }

    public String getName() {
        return name;
    }

    public String getFlagUrl() {
        return flagUrl;
    }
}
