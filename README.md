# Home Manager - Grocery List

A JavaFX desktop application for managing your grocery shopping lists, tracking prices, and organizing items by
categories.

![Java](https://img.shields.io/badge/Java-21%2B-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.4-green)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

## Features

### Grocery Management

- **Add, Edit & Remove Items** - Full CRUD operations for grocery items
- **Category Organization** - Organize items into customizable categories
- **Price Tracking** - Track item prices and calculate total costs
- **Quantity Management** - Set quantities for bulk pricing calculations
- **Location Tracking** - Remember where you typically buy each item

### Smart Filtering & Search

- **Category Filtering** - View items by specific categories
- **Real-time Search** - Search across names, descriptions, locations, and categories
- **Needed Items View** - Filter to show only items marked as needed
- **Show All Items** - Quick toggle to view entire inventory

### Data Management

- **Auto-save** - Automatically saves your data when changes are made
- **Import/Export** - JSON import/export for data backup and sharing
- **Persistent Storage** - Your data remains between application sessions

## 🚀 Installation

### Prerequisites

- Java 21 or later
- Gradle 8.4 or later

### Building from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/Chalwk/GroceryList.git
   cd grocery-list
   ```

2. **Build the application**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew run
   ```

### Creating a Native Installer

The project includes jpackage configuration for creating native installers:

```bash
./gradlew jpackage
```

This will create platform-specific installers:

- **Windows**: `.exe` installer
- **macOS**: `.dmg` or `.pkg`
- **Linux**: `.deb` or `.rpm`

## How to Use

### Adding Items

1. Click **"Add Item"** button
2. Enter item details:
    - Name
    - Price
    - Description
    - Quantity
    - Location
    - Category
3. Click **"Save"** to add to your list

### Managing Categories

- **Add Category**: Click "Add Category" and enter a name
- **Remove Category**: Select a category and click "Remove Category"
- **Default categories included**: Produce, Dairy, Meat, Bakery, Frozen, Pantry, Other

### Shopping Workflow

1. **Mark Needed Items**: Check the "Needed" checkbox for items you need
2. **View Total**: The top display shows total price of needed items
3. **Filter View**: Use "Items Needed" button to see only required items
4. **Export List**: Export your needed items for shopping

### Data Management

- **Import**: Load existing grocery data from JSON files
- **Export**: Save your current data to JSON for backup
- **Auto-save**: All changes are automatically saved

## Project Structure

```
src/main/java/com/chalwk/
├── Main.java                 # Application entry point
├── MainController.java       # Main UI controller
└── model/
    ├── GroceryItem.java      # Item data model
    ├── GroceryData.java      # Data management
    └── util/
        └── DataManager.java  # File I/O operations

src/main/resources/
├── main.fxml                # UI layout
├── styles.css               # Application styling
└── icon.ico                 # Application icon
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.