package com.chalwk.util;

import com.chalwk.model.GroceryData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class DataManager {
    private static final String DATA_FILE = "grocery_data.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void saveData(GroceryData data) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DATA_FILE), data);
            System.out.println("Data saved successfully to: " + DATA_FILE);
        } catch (Exception e) {
            System.err.println("Failed to save data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static GroceryData loadData() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                System.out.println("Loading data from: " + DATA_FILE);
                GroceryData data = objectMapper.readValue(file, GroceryData.class);
                System.out.println("Successfully loaded " + data.getItems().size() + " items and " + data.getCategories().size() + " categories");
                return data;
            } else {
                System.out.println("No existing data file found, starting with empty data");
            }
        } catch (Exception e) {
            System.err.println("Failed to load data: " + e.getMessage());
            e.printStackTrace();
            // Try to backup corrupted file
            backupCorruptedFile();
        }
        return new GroceryData();
    }

    private static void backupCorruptedFile() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                File backup = new File(DATA_FILE + ".corrupted");
                file.renameTo(backup);
                System.out.println("Corrupted file backed up as: " + backup.getName());
            }
        } catch (Exception e) {
            System.err.println("Failed to backup corrupted file: " + e.getMessage());
        }
    }
}