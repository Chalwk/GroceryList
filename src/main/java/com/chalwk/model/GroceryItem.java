package com.chalwk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroceryItem {
    private final StringProperty name;
    private final DoubleProperty price;
    private final StringProperty description;
    private final IntegerProperty quantity;
    private final StringProperty location;
    private final BooleanProperty needed;
    private final StringProperty category;

    @JsonCreator
    public GroceryItem(
            @JsonProperty("name") String name,
            @JsonProperty("price") double price,
            @JsonProperty("description") String description,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("location") String location,
            @JsonProperty("needed") boolean needed,
            @JsonProperty("category") String category) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.description = new SimpleStringProperty(description);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.location = new SimpleStringProperty(location);
        this.needed = new SimpleBooleanProperty(needed);
        this.category = new SimpleStringProperty(category);
    }

    public GroceryItem(String name, double price, String description, int quantity, String location, String category) {
        this(name, price, description, quantity, location, false, category);
    }

    // Standard getters
    public String getName() {
        return name.get();
    }

    // Setters
    public void setName(String name) {
        this.name.set(name);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public boolean isNeeded() {
        return needed.get();
    }

    public void setNeeded(boolean needed) {
        this.needed.set(needed);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public double getTotalPrice() {
        return getPrice() * getQuantity();
    }
}