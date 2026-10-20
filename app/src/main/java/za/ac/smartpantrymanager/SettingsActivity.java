package za.ac.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlerts;
    private Spinner spinnerPreferredUnit;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        spinnerPreferredUnit = findViewById(R.id.spinnerPreferredUnit);

        preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        switchExpiryAlerts.setChecked(
                preferences.getBoolean("expiry_alerts", true)
        );

        int savedUnit = preferences.getInt("preferred_unit", 0);

        if (savedUnit >= 0 &&
                savedUnit < spinnerPreferredUnit.getCount()) {
            spinnerPreferredUnit.setSelection(savedUnit);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnSaveSettings).setOnClickListener(v -> {
            boolean alertsEnabled = switchExpiryAlerts.isChecked();

            preferences.edit()
                    .putBoolean("expiry_alerts", alertsEnabled)
                    .putInt(
                            "preferred_unit",
                            spinnerPreferredUnit.getSelectedItemPosition()
                    )
                    .apply();

            if (alertsEnabled) {
                ExpiryReminderScheduler.schedule(this);
            } else {
                ExpiryReminderScheduler.cancel(this);
            }

            Toast.makeText(
                    SettingsActivity.this,
                    "Settings saved successfully",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}
