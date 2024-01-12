package com.example.havenus.SafetyCheckIn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.havenus.R;

public class NotificationHelper {
    public static final int VIBRATE_PERMISSION_REQUEST_CODE = 127;
    @SuppressLint("MissingPermission")
    public static void showNotification(Context context, String channelId, int notificationId) {
        // Check if the app has the required permission to show notifications
        if (hasPermissionToShowNotification(context)) {
            // Build and show the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Safety Check-In")
                    .setContentText("Please respond to the safety check-in.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());
            showToast(context, "Notification shown");

        } else {
            // Handle case where notification permission is not granted
            // For example, show a toast message
            showToast(context, "Notification permission not granted");
        }
    }

    public static boolean hasPermissionToShowNotification(Context context) {
        // Explicitly check for VIBRATE permission
        if (ContextCompat.checkSelfPermission(context, "android.permission.VIBRATE")
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Request VIBRATE permission if not granted
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.VIBRATE},
                    VIBRATE_PERMISSION_REQUEST_CODE);
            return false;
        }
    }


    // Helper method to show a toast message
    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}