package za.ac.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import za.ac.smartpantrymanager.adapter.PantryAdapter;
import za.ac.smartpantrymanager.database.DatabaseHelper;
import za.ac.smartpantrymanager.model.PantryItem;

public class MyPantryActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private RecyclerView recyclerPantry;
    private TextView txtEmpty;
    private EditText edtPantrySearch;

    private final ArrayList<PantryItem> allPantryItems =
            new ArrayList<>();

    private final ArrayList<PantryItem> pantryItems =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pantry);

        recyclerPantry = findViewById(R.id.recyclerMyPantry);
        txtEmpty = findViewById(R.id.txtMyPantryEmpty);
        edtPantrySearch = findViewById(R.id.edtPantrySearch);


        View btnHome = findViewById(R.id.btnPantryHome);
        View fabAdd = findViewById(R.id.fabAddIngredient);

        db = new DatabaseHelper(this);

        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new PantryAdapter(
                pantryItems,
                this::editIngredient,
                this::deleteIngredient
        );

        recyclerPantry.setAdapter(adapter);

        // Return to the dashboard.
        btnHome.setOnClickListener(v -> finish());


        // Floating Add Ingredient button.
        fabAdd.setOnClickListener(v -> openAddIngredient());

        // Search ingredients by name.
        edtPantrySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                filterPantry(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadPantry();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && adapter != null) {
            loadPantry();
        }
    }

    private void openAddIngredient() {
        Intent intent = new Intent(
                MyPantryActivity.this,
                AddEditIngredientActivity.class
        );

        startActivity(intent);
    }

    private void loadPantry() {
        allPantryItems.clear();

        try (Cursor cursor = db.getAllPantryItems()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    allPantryItems.add(new PantryItem(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getDouble(2),
                            cursor.getString(3),
                            cursor.getString(4)
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Could not load pantry ingredients.",
                    Toast.LENGTH_LONG
            ).show();
        }

        filterPantry(edtPantrySearch.getText().toString());
    }

    private void filterPantry(String query) {
        pantryItems.clear();

        String search = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        for (PantryItem item : allPantryItems) {
            String name = item.getName() == null
                    ? ""
                    : item.getName().toLowerCase(Locale.ROOT);

            if (name.contains(search)) {
                pantryItems.add(item);
            }
        }

        adapter.notifyDataSetChanged();

        boolean isEmpty = pantryItems.isEmpty();
        txtEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (allPantryItems.isEmpty()) {
            txtEmpty.setText(
                    "Your pantry is empty.\nAdd an ingredient to get started."
            );
        } else if (isEmpty) {
            txtEmpty.setText(
                    "No ingredients found.\nTry another search."
            );
        }
    }

    private void editIngredient(PantryItem item) {
        Intent intent = new Intent(
                MyPantryActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra("ingredient_id", item.getId());
        intent.putExtra("ingredient_name", item.getName());
        intent.putExtra("ingredient_quantity", item.getQuantity());
        intent.putExtra("ingredient_unit", item.getUnit());
        intent.putExtra("ingredient_expiry", item.getExpiryDate());

        startActivity(intent);
    }

    private void deleteIngredient(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage(
                        "Remove " + item.getName()
                                + " from your pantry?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    int deleted = db.deletePantryItem(item.getId());

                    if (deleted > 0) {
                        loadPantry();

                        Toast.makeText(
                                MyPantryActivity.this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                MyPantryActivity.this,
                                "Could not delete ingredient.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }

        super.onDestroy();
    }
}
