package za.ac.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import za.ac.smartpantrymanager.adapter.RecipeAdapter;
import za.ac.smartpantrymanager.database.DatabaseHelper;
import za.ac.smartpantrymanager.model.PantryItem;
import za.ac.smartpantrymanager.model.Recipe;
import za.ac.smartpantrymanager.utils.RecipeMatcher;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private TextView txtNoRecipes;
    private RecyclerView recyclerRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        database = new DatabaseHelper(this);

        txtNoRecipes = findViewById(R.id.txtNoRecipes);
        recyclerRecipes = findViewById(R.id.recyclerRecipes);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        findViewById(R.id.btnBackRecipes)
                .setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        ArrayList<PantryItem> pantry = new ArrayList<>();
        ArrayList<Recipe> recipes = new ArrayList<>();

        try (Cursor cursor = database.getAllPantryItems()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    pantry.add(new PantryItem(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getDouble(2),
                            cursor.getString(3),
                            cursor.getString(4)
                    ));
                }
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "Could not load pantry ingredients.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try (Cursor cursor = database.getAllRecipes()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    recipes.add(new Recipe(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4)
                    ));
                }
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "Could not load recipes.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (pantry.isEmpty()) {
            showEmptyMessage(
                    "Your pantry is empty.\n"
                            + "Add ingredients first to discover recipes."
            );
            return;
        }

        if (recipes.isEmpty()) {
            showEmptyMessage(
                    "No recipes are available yet.\n"
                            + "Add recipes to your recipe database."
            );
            return;
        }

        ArrayList<Recipe> matches =
                RecipeMatcher.getMatchingRecipes(pantry, recipes);

        if (matches.isEmpty()) {
            showEmptyMessage(
                    "No recipes match your current pantry ingredients.\n\n"
                            + "Add more ingredients or update your quantities "
                            + "to discover more recipes."
            );
            return;
        }

        txtNoRecipes.setVisibility(View.GONE);
        recyclerRecipes.setVisibility(View.VISIBLE);
        recyclerRecipes.setAdapter(
                new RecipeAdapter(this, matches)
        );
    }

    private void showEmptyMessage(String message) {
        txtNoRecipes.setText(message);
        txtNoRecipes.setVisibility(View.VISIBLE);
        recyclerRecipes.setVisibility(View.GONE);
    }
}
