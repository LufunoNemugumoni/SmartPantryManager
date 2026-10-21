package za.ac.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages the Smart Pantry Manager's local SQLite database.
 *
 * Stores pantry ingredients and preloaded recipes. Provides methods
 * for creating, reading, updating and deleting pantry records.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version used by SQLiteOpenHelper.
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Opens or creates the application's SQLite database.
     *
     * @param c application context
     */
    public DatabaseHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the pantry and recipe tables on the first database creation.
     * Preloads 20 recipes so users can receive suggestions immediately.
     *
     * @param d database in which the tables will be created
     */
    @Override
    public void onCreate(SQLiteDatabase d) {

        // Stores ingredients currently available in the user's pantry.
        d.execSQL(
                "CREATE TABLE pantry_items (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "ingredient_name TEXT NOT NULL," +
                        "quantity REAL NOT NULL," +
                        "unit TEXT NOT NULL," +
                        "expiry_date TEXT)"
        );

        // Stores recipe information and comma-separated ingredient data.
        d.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "recipe_name TEXT NOT NULL," +
                        "ingredients TEXT NOT NULL," +
                        "quantities TEXT NOT NULL," +
                        "instructions TEXT NOT NULL)"
        );

        // Each recipe contains its name, required ingredients,
        // corresponding quantities, and preparation instructions.
        String[][] recipes = {
                {"Vegetable Omelette", "egg,tomato,onion", "2,1,1",
                        "Whisk eggs. Cook tomato and onion. Add eggs."},
                {"Tomato Pasta", "pasta,tomato,garlic", "200,2,1",
                        "Boil pasta. Cook tomato and garlic. Combine."},
                {"Fried Rice", "rice,egg,onion", "200,2,1",
                        "Cook onion. Add rice and egg. Stir-fry."},
                {"Chicken Stir Fry", "chicken,onion,pepper", "200,1,1",
                        "Cook chicken. Add vegetables and stir-fry."},
                {"Potato Curry", "potato,onion,tomato", "300,1,2",
                        "Cook ingredients with spices until tender."},
                {"Tuna Sandwich", "bread,tuna,tomato", "2,1,1",
                        "Fill bread with tuna and tomato."},
                {"Vegetable Soup", "carrot,potato,onion", "2,2,1",
                        "Simmer vegetables until soft."},
                {"Egg Fried Rice", "rice,egg,carrot", "200,2,1",
                        "Stir-fry rice with egg and carrot."},
                {"Tomato Salad", "tomato,onion", "2,1",
                        "Chop and mix."},
                {"Chicken Salad", "chicken,lettuce,tomato", "200,2,1",
                        "Cook chicken and combine with vegetables."},
                {"Garlic Bread", "bread,garlic,butter", "2,1,1",
                        "Spread garlic butter and toast."},
                {"Pancakes", "flour,milk,egg", "200,250,2",
                        "Mix batter and cook."},
                {"Scrambled Eggs", "egg,butter", "2,1",
                        "Whisk eggs and cook with butter."},
                {"Pasta Primavera", "pasta,carrot,pepper", "200,1,1",
                        "Boil pasta and saute vegetables."},
                {"Potato Omelette", "potato,egg,onion", "200,3,1",
                        "Cook potato and onion, add eggs."},
                {"Rice and Beans", "rice,beans,tomato", "200,200,1",
                        "Cook rice and combine with beans."},
                {"Vegetable Curry", "potato,carrot,onion", "200,2,1",
                        "Cook vegetables with curry spices."},
                {"Grilled Cheese Sandwich", "bread,cheese,butter", "2,2,1",
                        "Butter bread, add cheese and grill."},
                {"Banana Pancakes", "banana,flour,egg", "2,150,2",
                        "Mash banana, mix batter and cook."},
                {"Chicken Wrap", "chicken,lettuce,tomato", "200,2,1",
                        "Cook chicken and wrap with vegetables."}
        };

        // Insert the initial recipe collection into SQLite.
        // This method runs when the database is created for the first time.
        for (String[] recipe : recipes) {
            ContentValues values = new ContentValues();
            values.put("recipe_name", recipe[0]);
            values.put("ingredients", recipe[1]);
            values.put("quantities", recipe[2]);
            values.put("instructions", recipe[3]);

            d.insert("recipes", null, values);
        }
    }

    /**
     * Handles database version upgrades.
     *
     * No migration is required for database version 1.
     * Add migration logic here if the schema changes in a future version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase d, int oldVersion, int newVersion) {
        // Preserve existing user data when no schema migration is needed.
    }

    /**
     * Adds a new ingredient to the pantry.
     *
     * @param n ingredient name
     * @param q available quantity
     * @param u quantity unit
     * @param e optional expiry date
     * @return inserted row ID, or -1 if insertion fails
     */
    public long addPantryItem(String n, double q, String u, String e) {
        ContentValues values = new ContentValues();
        values.put("ingredient_name", n);
        values.put("quantity", q);
        values.put("unit", u);
        values.put("expiry_date", e);

        return getWritableDatabase().insert(
                "pantry_items", null, values
        );
    }

    /**
     * Retrieves all pantry ingredients in alphabetical order.
     *
     * @return cursor containing pantry records
     */
    public Cursor getAllPantryItems() {
        return getReadableDatabase().query(
                "pantry_items", null, null, null, null, null,
                "ingredient_name ASC"
        );
    }

    /**
     * Updates an existing pantry ingredient using its ID.
     *
     * @param id database ID of the ingredient
     * @param n updated ingredient name
     * @param q updated quantity
     * @param u updated unit
     * @param e updated expiry date
     * @return number of rows updated
     */
    public int updatePantryItem(
            int id, String n, double q, String u, String e) {

        ContentValues values = new ContentValues();
        values.put("ingredient_name", n);
        values.put("quantity", q);
        values.put("unit", u);
        values.put("expiry_date", e);

        return getWritableDatabase().update(
                "pantry_items",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Deletes a pantry ingredient by its database ID.
     *
     * @param id ID of the ingredient to delete
     * @return number of rows deleted
     */
    public int deletePantryItem(int id) {
        return getWritableDatabase().delete(
                "pantry_items",
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Retrieves all saved recipes in alphabetical order.
     *
     * @return cursor containing recipe records
     */
    public Cursor getAllRecipes() {
        return getReadableDatabase().query(
                "recipes", null, null, null, null, null,
                "recipe_name ASC"
        );
    }
}
