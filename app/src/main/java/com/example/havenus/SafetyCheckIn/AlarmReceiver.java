package com.example.havenus.SafetyCheckIn;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "SafetyCheckInChannel";
    private static int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm received");

        // Check for notification permission
        if (NotificationHelper.hasPermissionToShowNotification(context)) {
            // Show notification using the NotificationManager
            NotificationHelper.showNotification(context, CHANNEL_ID, NOTIFICATION_ID);
            NOTIFICATION_ID++;
        } else {
            // Handle case where notification permission is not granted
            // You can take any other appropriate action
            Log.e("AlarmReceiver", "Notification permission not granted");
        }
    }
}
