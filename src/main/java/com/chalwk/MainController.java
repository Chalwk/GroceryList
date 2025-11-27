package com.chalwk;

import com.chalwk.model.GroceryData;
import com.chalwk.model.GroceryItem;
import com.chalwk.util.DataManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    @FXML
    private TableView<GroceryItem> itemsTable;
    @FXML
    private TableColumn<GroceryItem, String> nameColumn;
    @FXML
    private TableColumn<GroceryItem, Double> priceColumn;
    @FXML
    private TableColumn<GroceryItem, String> descriptionColumn;
    @FXML
    private TableColumn<GroceryItem, Integer> quantityColumn;
    @FXML
    private TableColumn<GroceryItem, String> locationColumn;
    @FXML
    private TableColumn<GroceryItem, Boolean> neededColumn;
    @FXML
    private TableColumn<GroceryItem, String> categoryColumn;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private Label totalLabel;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField searchField;

    private GroceryData groceryData;
    private FilteredList<GroceryItem> filteredItems;
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        groceryData = DataManager.loadData();
        setupTable();
        setupBindings();
        setupFilters();
        updateTotal();
    }

    private void setupTable() {
        // Configure table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        neededColumn.setCellValueFactory(new PropertyValueFactory<>("needed"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Format price column
        priceColumn.setCellFactory(column -> new TableCell<GroceryItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });

        // Make needed column checkboxes
        neededColumn.setCellFactory(column -> new TableCell<GroceryItem, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    GroceryItem item = getTableView().getItems().get(getIndex());
                    item.setNeeded(checkBox.isSelected());
                    updateTotal();
                    saveData();
                });
            }

            @Override
            protected void updateItem(Boolean needed, boolean empty) {
                super.updateItem(needed, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(needed != null && needed);
                    setGraphic(checkBox);
                }
            }
        });

        filteredItems = new FilteredList<>(groceryData.getItems());
        itemsTable.setItems(filteredItems);
    }

    private void setupBindings() {
        // Bind categories to combo boxes
        categoryComboBox.setItems(groceryData.getCategories());
        categoryFilter.setItems(groceryData.getCategories());

        // Update total when items change
        groceryData.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends GroceryItem> c) -> {
            updateTotal();
            saveData();
        });
    }

    private void setupFilters() {
        // Category filter
        categoryFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        // Search filter
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String categoryFilterValue = categoryFilter.getSelectionModel().getSelectedItem();
        String searchText = searchField.getText().toLowerCase();

        filteredItems.setPredicate(item -> {
            // Category filter
            if (categoryFilterValue != null && !categoryFilterValue.equals("All") &&
                    !categoryFilterValue.equals(item.getCategory())) {
                return false;
            }

            // Search filter
            if (searchText != null && !searchText.isEmpty()) {
                return item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText) ||
                        item.getLocation().toLowerCase().contains(searchText) ||
                        item.getCategory().toLowerCase().contains(searchText);
            }

            return true;
        });
    }

    private void updateTotal() {
        double total = groceryData.calculateTotalNeeded();
        totalLabel.setText("TOTAL PRICE: " + currencyFormat.format(total));
    }

    private void saveData() {
        DataManager.saveData(groceryData);
    }

    @FXML
    private void showAllItems() {
        categoryFilter.getSelectionModel().clearSelection();
        searchField.clear();
        filteredItems.setPredicate(item -> true);
    }

    @FXML
    private void showNeededItems() {
        searchField.clear();
        filteredItems.setPredicate(GroceryItem::isNeeded);
    }

    @FXML
    private void addItem() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Item");
        nameDialog.setHeaderText("Enter item name:");
        nameDialog.setContentText("Name:");

        nameDialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Dialog<GroceryItem> dialog = createItemDialog(name.trim(), null);
                dialog.showAndWait().ifPresent(item -> {
                    groceryData.addItem(item);
                    itemsTable.refresh();
                    saveData();
                });
            }
        });
    }

    @FXML
    private void editItem() {
        GroceryItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dialog<GroceryItem> dialog = createItemDialog(selected.getName(), selected);
            dialog.showAndWait().ifPresent(item -> {
                // Update the existing item
                selected.setName(item.getName());
                selected.setPrice(item.getPrice());
                selected.setDescription(item.getDescription());
                selected.setQuantity(item.getQuantity());
                selected.setLocation(item.getLocation());
                selected.setCategory(item.getCategory());
                itemsTable.refresh();
                updateTotal();
                saveData();
            });
        } else {
            showAlert("No Selection", "Please select an item to edit.");
        }
    }

    @FXML
    private void removeItem() {
        GroceryItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            groceryData.removeItem(selected);
            updateTotal();
            saveData();
        } else {
            showAlert("No Selection", "Please select an item to remove.");
        }
    }

    @FXML
    private void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter new category name:");
        dialog.setContentText("Category:");

        dialog.showAndWait().ifPresent(category -> {
            if (!category.trim().isEmpty()) {
                groceryData.addCategory(category.trim());
                saveData();
            }
        });
    }

    @FXML
    private void removeCategory() {
        String selected = categoryFilter.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.equals("All")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Category");
            alert.setHeaderText("Remove Category: " + selected);
            alert.setContentText("This will also remove all items in this category. Continue?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    groceryData.removeCategory(selected);
                    saveData();
                }
            });
        } else {
            showAlert("No Selection", "Please select a category to remove.");
        }
    }

    @FXML
    private void exportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Grocery Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, groceryData);
                showAlert("Success", "Data exported successfully to: " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Error", "Failed to export data: " + e.getMessage());
            }
        }
    }

    @FXML
    private void importData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Grocery Data");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                GroceryData importedData = objectMapper.readValue(file, GroceryData.class);
                groceryData.getItems().setAll(importedData.getItems());
                groceryData.getCategories().setAll(importedData.getCategories());
                itemsTable.refresh();
                updateTotal();
                saveData();
                showAlert("Success", "Data imported successfully from: " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Error", "Failed to import data: " + e.getMessage());
            }
        }
    }

    private Dialog<GroceryItem> createItemDialog(String name, GroceryItem existingItem) {
        Dialog<GroceryItem> dialog = new Dialog<>();
        dialog.setTitle(existingItem == null ? "Add Item" : "Edit Item");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form
        TextField nameField = new TextField(name);
        TextField priceField = new TextField(existingItem != null ? String.valueOf(existingItem.getPrice()) : "0.0");
        TextArea descriptionArea = new TextArea(existingItem != null ? existingItem.getDescription() : "");
        TextField quantityField = new TextField(existingItem != null ? String.valueOf(existingItem.getQuantity()) : "1");
        TextField locationField = new TextField(existingItem != null ? existingItem.getLocation() : "");
        ComboBox<String> categoryBox = new ComboBox<>(groceryData.getCategories());

        if (existingItem != null) {
            categoryBox.setValue(existingItem.getCategory());
        } else if (!groceryData.getCategories().isEmpty()) {
            categoryBox.setValue(groceryData.getCategories().getFirst());
        }

        descriptionArea.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Location:"), 0, 4);
        grid.add(locationField, 1, 4);
        grid.add(new Label("Category:"), 0, 5);
        grid.add(categoryBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new GroceryItem(
                            nameField.getText(),
                            Double.parseDouble(priceField.getText()),
                            descriptionArea.getText(),
                            Integer.parseInt(quantityField.getText()),
                            locationField.getText(),
                            categoryBox.getValue()
                    );
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for price and quantity.");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}