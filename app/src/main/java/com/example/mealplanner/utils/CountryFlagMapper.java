package com.example.mealplanner.utils;

import java.util.HashMap;
import java.util.Map;

public class CountryFlagMapper {
    private static final Map<String, String> AREA_TO_CODE = new HashMap<>();

    static {
        AREA_TO_CODE.put("American", "us");
        AREA_TO_CODE.put("British", "gb");
        AREA_TO_CODE.put("Canadian", "ca");
        AREA_TO_CODE.put("Chinese", "cn");
        AREA_TO_CODE.put("Croatian", "hr");
        AREA_TO_CODE.put("Dutch", "nl");
        AREA_TO_CODE.put("Egyptian", "eg");
        AREA_TO_CODE.put("Filipino", "ph");
        AREA_TO_CODE.put("French", "fr");
        AREA_TO_CODE.put("Greek", "gr");
        AREA_TO_CODE.put("Indian", "in");
        AREA_TO_CODE.put("Irish", "ie");
        AREA_TO_CODE.put("Italian", "it");
        AREA_TO_CODE.put("Jamaican", "jm");
        AREA_TO_CODE.put("Japanese", "jp");
        AREA_TO_CODE.put("Kenyan", "ke");
        AREA_TO_CODE.put("Malaysian", "my");
        AREA_TO_CODE.put("Mexican", "mx");
        AREA_TO_CODE.put("Moroccan", "ma");
        AREA_TO_CODE.put("Polish", "pl");
        AREA_TO_CODE.put("Portuguese", "pt");
        AREA_TO_CODE.put("Russian", "ru");
        AREA_TO_CODE.put("Spanish", "es");
        AREA_TO_CODE.put("Thai", "th");
        AREA_TO_CODE.put("Tunisian", "tn");
        AREA_TO_CODE.put("Turkish", "tr");
        AREA_TO_CODE.put("Ukrainian", "ua");
        AREA_TO_CODE.put("Vietnamese", "vn");
        AREA_TO_CODE.put("Algerian", "dz");
        AREA_TO_CODE.put("Argentinian", "ar");
        AREA_TO_CODE.put("Australian", "au");
        AREA_TO_CODE.put("Saudi Arabian", "sa");
        AREA_TO_CODE.put("Slovakian", "sk");
        AREA_TO_CODE.put("Syrian", "sy");
        AREA_TO_CODE.put("Uruguayan", "uy");
        AREA_TO_CODE.put("Venezuelan", "ve");
        AREA_TO_CODE.put("Venezuela", "ve");
        AREA_TO_CODE.put("Venezulan", "ve");
        AREA_TO_CODE.put("Norwegian", "no");
    }

    public static String getFlagUrl(String areaName) {
        String countryCode = AREA_TO_CODE.get(areaName);
        if (countryCode == null) {
            countryCode = "un"; // United Nations flag as fallback
        }
        return "https://flagcdn.com/w320/" + countryCode + ".png";
    }

    public static String getCountryCode(String areaName) {
        return AREA_TO_CODE.getOrDefault(areaName, "un");
    }
}
