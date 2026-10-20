package za.ac.smartpantrymanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import za.ac.smartpantrymanager.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;

    private TextView totalIngredients;
    private TextView expiringSoon;
    private TextView expiredIngredients;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        totalIngredients = findViewById(R.id.txtTotalIngredients);
        expiringSoon = findViewById(R.id.txtExpiringSoon);
        expiredIngredients = findViewById(R.id.txtExpiredIngredients);

        // Header settings icon
        ImageButton settingsIcon = findViewById(R.id.imgTopSettings);
        settingsIcon.setOnClickListener(v -> openSettings());

        // Make the whole My Pantry card clickable.
        findViewById(R.id.cardMyPantry).setOnClickListener(v ->
                openScreen(MyPantryActivity.class));

        // Make the whole Recipes card clickable.
        findViewById(R.id.cardRecipes).setOnClickListener(v ->
                openScreen(SuggestedRecipesActivity.class));

        // Make the whole Add Ingredient card clickable.
        findViewById(R.id.cardAddIngredient).setOnClickListener(v ->
                openScreen(AddEditIngredientActivity.class));

        // Make the whole Settings card clickable.
        findViewById(R.id.cardSettings).setOnClickListener(v ->
                openSettings());

        // Summary cards
        findViewById(R.id.cardTotalIngredients).setOnClickListener(v ->
                openScreen(MyPantryActivity.class));

        findViewById(R.id.cardExpiringSoon).setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "Ingredients expiring within 3 days: "
                            + expiringSoon.getText(),
                    Toast.LENGTH_SHORT
            ).show();

            openScreen(MyPantryActivity.class);
        });

        findViewById(R.id.cardExpiredIngredients).setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "Expired ingredients: " + expiredIngredients.getText(),
                    Toast.LENGTH_SHORT
            ).show();

            openScreen(MyPantryActivity.class);
        });

        // Schedule expiry reminders.
        try {
            ExpiryReminderScheduler.schedule(this);
        } catch (Exception e) {
            // Dashboard remains usable if reminders cannot be scheduled.
            e.printStackTrace();
        }

        requestNotificationPermission();
        updateDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && totalIngredients != null) {
            updateDashboard();
        }
    }

    private void openScreen(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private void openSettings() {
        openScreen(SettingsActivity.class);
    }

    private void updateDashboard() {
        int total = 0;
        int expiring = 0;
        int expired = 0;

        Calendar today = Calendar.getInstance();
        clearTime(today);
        Date todayDate = today.getTime();

        Calendar limitCalendar = Calendar.getInstance();
        clearTime(limitCalendar);
        limitCalendar.add(Calendar.DAY_OF_YEAR, 3);
        Date expiryLimit = limitCalendar.getTime();

        try (Cursor cursor = db.getAllPantryItems()) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    total++;

                    // Expiry date is column index 4 in the existing database.
                    String expiryString = cursor.getString(4);

                    if (expiryString == null || expiryString.trim().isEmpty()) {
                        continue;
                    }

                    Date parsedDate = parseExpiryDate(expiryString);

                    if (parsedDate == null) {
                        continue;
                    }

                    Calendar expiryCalendar = Calendar.getInstance();
                    expiryCalendar.setTime(parsedDate);
                    clearTime(expiryCalendar);
                    Date expiryDate = expiryCalendar.getTime();

                    if (expiryDate.before(todayDate)) {
                        expired++;
                    } else if (!expiryDate.after(expiryLimit)) {
                        expiring++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Unable to load pantry summary.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        totalIngredients.setText(String.valueOf(total));
        expiringSoon.setText(String.valueOf(expiring));
        expiredIngredients.setText(String.valueOf(expired));
    }

    private Date parseExpiryDate(String dateString) {
        String[] formats = {
                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy",
                "d/M/yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "d-M-yyyy"
        };

        for (String format : formats) {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    format,
                    Locale.getDefault()
            );

            sdf.setLenient(false);

            try {
                return sdf.parse(dateString.trim());
            } catch (ParseException ignored) {
                // Try the next supported format.
            }
        }

        return null;
    }

    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_REQUEST
                );
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }

        super.onDestroy();
    }
}
