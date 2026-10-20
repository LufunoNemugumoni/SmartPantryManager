package za.ac.smartpantrymanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import za.ac.smartpantrymanager.database.DatabaseHelper;

public class ExpiryReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "pantry_expiry_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences(
                "SmartPantrySettings", Context.MODE_PRIVATE);

        if (!preferences.getBoolean("expiry_alerts", true)) {
            return;
        }

        DatabaseHelper db = new DatabaseHelper(context);
        StringBuilder expired = new StringBuilder();
        StringBuilder upcoming = new StringBuilder();

        Calendar today = Calendar.getInstance();
        clearTime(today);

        Calendar limit = (Calendar) today.clone();
        limit.add(Calendar.DAY_OF_YEAR, 3);

        try (Cursor cursor = db.getAllPantryItems()) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                String expiry = cursor.getString(4);

                if (expiry == null || expiry.trim().isEmpty()) {
                    continue;
                }

                SimpleDateFormat format =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                format.setLenient(false);

                Date expiryDate;

                try {
                    expiryDate = format.parse(expiry.trim());
                } catch (ParseException e) {
                    continue;
                }

                if (expiryDate == null) {
                    continue;
                }

                Calendar expiryCalendar = Calendar.getInstance();
                expiryCalendar.setTime(expiryDate);
                clearTime(expiryCalendar);

                if (expiryCalendar.before(today)) {
                    appendItem(expired, name);
                } else if (!expiryCalendar.after(limit)) {
                    appendItem(upcoming, name + " (" + expiry + ")");
                }
            }
        } catch (Exception e) {
            return;
        }

        StringBuilder message = new StringBuilder();

        if (expired.length() > 0) {
            message.append("Expired: ").append(expired);
        }

        if (upcoming.length() > 0) {
            if (message.length() > 0) {
                message.append(". ");
            }
            message.append("Expiring soon: ").append(upcoming);
        }

        if (message.length() == 0) {
            return;
        }

        createNotificationChannel(context);

        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                2002,
                openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("Smart Pantry expiry reminder")
                        .setContentText(message.toString())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message.toString()))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context)
                    .notify(3001, notification.build());
        } catch (SecurityException ignored) {
        }
    }

    private static void appendItem(StringBuilder builder, String item) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(item);
    }

    private static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pantry expiry reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(
                    "Reminders for expired and soon-to-expire pantry ingredients");

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
