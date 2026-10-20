package za.ac.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import za.ac.smartpantrymanager.R;
import za.ac.smartpantrymanager.database.DatabaseHelper;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText n, q, e;
    private Spinner u;
    private DatabaseHelper db;
    private int id = -1;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_edit_ingredient);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        db = new DatabaseHelper(this);

        n = findViewById(R.id.edtIngredientName);
        q = findViewById(R.id.edtQuantity);
        u = findViewById(R.id.spinnerUnit);
        e = findViewById(R.id.edtExpiryDate);

        if (getIntent().hasExtra("ingredient_id")) {
            id = getIntent().getIntExtra("ingredient_id", -1);

            n.setText(getIntent().getStringExtra("ingredient_name"));
            q.setText(String.valueOf(
                    getIntent().getDoubleExtra("ingredient_quantity", 0)
            ));
            e.setText(getIntent().getStringExtra("ingredient_expiry"));

            String selectedUnit =
                    getIntent().getStringExtra("ingredient_unit");

            String[] units = getResources()
                    .getStringArray(R.array.unit_options);

            for (int i = 0; i < units.length; i++) {
                if (units[i].equals(selectedUnit)) {
                    u.setSelection(i);
                    break;
                }
            }
        }

        e.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            new DatePickerDialog(
                    this,
                    (picker, year, month, day) ->
                            e.setText(String.format(
                                    Locale.US,
                                    "%04d-%02d-%02d",
                                    year, month + 1, day
                            )),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        findViewById(R.id.btnSaveIngredient)
                .setOnClickListener(v -> save());

        findViewById(R.id.btnCancel)
                .setOnClickListener(v -> finish());
    }

    private void save() {
        String name = n.getText().toString().trim();

        if (name.isEmpty()) {
            n.setError("Enter ingredient name");
            return;
        }

        String quantityText = q.getText().toString().trim()
                .replace(',', '.');

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException ex) {
            q.setError("Enter a valid number");
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            q.setError("Quantity must be a valid number greater than 0");
            return;
        }

        if (u.getSelectedItem() == null) {
            Toast.makeText(
                    this,
                    "Select a unit",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String unit = u.getSelectedItem().toString();
        String expiry = e.getText().toString().trim();

        if (id < 0) {
            db.addPantryItem(name, quantity, unit, expiry);
        } else {
            db.updatePantryItem(id, name, quantity, unit, expiry);
        }

        Toast.makeText(
                this,
                "Ingredient saved",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}

