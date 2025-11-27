package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroceryData {
    private final ObservableList<GroceryItem> items;
    private final ObservableList<String> categories;

    @JsonCreator
    public GroceryData(
            @JsonProperty("items") List<GroceryItem> items,
            @JsonProperty("categories") List<String> categories) {
        this.items = FXCollections.observableArrayList(items != null ? items : new ArrayList<>());
        this.categories = FXCollections.observableArrayList(categories != null ? categories : new ArrayList<>());

        if (this.categories.isEmpty()) {
            this.categories.addAll("Produce", "Dairy", "Meat", "Bakery", "Frozen", "Pantry", "Other");
        }
    }

    public GroceryData() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public ObservableList<GroceryItem> getItems() {
        return items;
    }

    public ObservableList<String> getCategories() {
        return categories;
    }

    public void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(String category) {
        categories.remove(category);
        items.removeIf(item -> category.equals(item.getCategory()));
    }

    public void addItem(GroceryItem item) {
        items.add(item);
    }

    public void removeItem(GroceryItem item) {
        items.remove(item);
    }

    public double calculateTotalNeeded() {
        return items.stream()
                .filter(GroceryItem::isNeeded)
                .mapToDouble(GroceryItem::getTotalPrice)
                .sum();
    }
}