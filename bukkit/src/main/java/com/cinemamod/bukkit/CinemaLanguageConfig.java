package com.cinemamod.bukkit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CinemaLanguageConfig {

    private final Map<String, String> messages = new HashMap<>();

    public CinemaLanguageConfig(File languageFile) {
        try {
            if (!languageFile.exists()) {
                throw new RuntimeException("Language file not found: " + languageFile.getName());
            }

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(new FileReader(languageFile), JsonObject.class);

            for (String key : json.keySet()) {
                messages.put(key, json.get(key).getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessage(String key, String defaultValue) {
        return messages.getOrDefault(key, defaultValue);
    }
}
