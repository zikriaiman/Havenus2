package com.example.havenus.SafetyCheckIn;

import static com.example.havenus.SafetyCheckIn.NotificationHelper.VIBRATE_PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.havenus.R;

import java.util.ArrayList;
public class SafetyCheckInActivation extends AppCompatActivity {
    private static final String CHANNEL_ID = "SafetyCheckInChannel";
    public static int NOTIFICATION_ID = 1;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ListView contactListView;

    public static final String PREFS_NAME = "SafetyCheckInPrefs";
    private static final String KEY_INTERVAL = "selectedInterval";
    public static final String KEY_ACTIVATED = "activated";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int YOUR_PERMISSION_REQUEST_CODE = 111;
    private static final int CONTACT_PICKER_REQUEST = 101;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1003;

    private static final int NOTIFICATION_RESPONSE_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds
    private static final String KEY_LAST_NOTIFICATION_TIMESTAMP = "lastNotificationTimestamp";
    long timeInterval;
    static ArrayList<String> selectedContacts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_check_in_activation);

        createNotificationChannel();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        RadioButton radioButton1 = findViewById(R.id.radioButton1);
        RadioButton radioButton2 = findViewById(R.id.radioButton2);
        RadioButton radioButton3 = findViewById(R.id.radioButton3);
        RadioButton radioButton4 = findViewById(R.id.radioButton4);
        RadioButton radioButton5 = findViewById(R.id.radioButton5);

        contactListView = findViewById(R.id.contactListView);
        ImageButton chooseContactsButton = findViewById(R.id.chooseContactsButton);

        // Initialize an ArrayList to store selected contacts
        selectedContacts = new ArrayList<>();

        // Initialize ArrayAdapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedContacts);
        contactListView.setAdapter(adapter);

        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInterval = 1 * 60 * 1000;
                //scheduleAlarm(1 * 60 * 1000); // 1 minute in milliseconds
                saveInterval(1);
                showToast("Safety Check-In Activated for 1 minute");
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInterval = 30 * 60 * 1000;
                //scheduleAlarm(30 * 60 * 1000); // 30 minutes in milliseconds
                saveInterval(30);
                showToast("Safety Check-In Activated for 30 minutes");
            }
        });
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInterval = 60 * 60 * 1000;
                //scheduleAlarm(60 * 60 * 1000); // 60 minutes in milliseconds
                saveInterval(60);
                showToast("Safety Check-In Activated for 1 hour");
            }
        });
        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInterval = 180 * 60 * 1000;
                //scheduleAlarm(180 * 60 * 1000); // 180 minutes in milliseconds
                saveInterval(180);
                showToast("Safety Check-In Activated for 3 hours");
            }
        });
        radioButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInterval = 480 * 60 * 1000;
                //scheduleAlarm(480 * 60 * 1000); // 480 minutes in milliseconds
                saveInterval(480);
                showToast("Safety Check-In Activated for 8 hours");
            }
        });
        chooseContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the contact picker
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CONTACT_PICKER_REQUEST);
            }
        });
        // Handle item click in the ListView to remove selected contacts
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedContacts.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        int permissionCheck = ContextCompat.checkSelfPermission(this, "com.android.alarm.permission.SET_ALARM");
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"com.android.alarm.permission.SET_ALARM"}, PERMISSION_REQUEST_CODE);
        }
        // Check and request VIBRATE permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.VIBRATE},
                    VIBRATE_PERMISSION_REQUEST_CODE);
        }

        // Check and request USE_FULL_SCREEN_INTENT permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FULL_SCREEN_INTENT)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.USE_FULL_SCREEN_INTENT},
                    YOUR_PERMISSION_REQUEST_CODE);
        }
        Button btnActivate = findViewById(R.id.btnActivate);
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save activation state
                saveActivationState(true);

                scheduleAlarm(timeInterval);

                // Launch the next activity
                Intent intent = new Intent(SafetyCheckInActivation.this, SafetyCheckInDeactivation.class);
                startActivity(intent);

                // Check if the user has not responded to the last notification within the allowed time
                //checkForAlerts(selectedContacts);
            }
        });
    }

    public static ArrayList<String> getSelectedContacts() {
        return selectedContacts;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now proceed with scheduling alarms
            } else {
                showToast("Permission denied. Safety Check-In feature requires permission to set alarms.");
            }
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SafetyCheckInChannel";
            String description = "Safety Check-In Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleAlarm(long intervalMillis) {
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + intervalMillis,
                intervalMillis, pendingIntent);
        long triggerTime = SystemClock.elapsedRealtime() + intervalMillis;
        Log.d("AlarmScheduling", "Scheduling alarm with interval: " + intervalMillis);

        // Save the timestamp when the notification is scheduled
        saveNotificationTimestamp();
    }

    private void saveInterval(int interval) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_INTERVAL, interval);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICKER_REQUEST && resultCode == RESULT_OK) {
            // Handle the contact picker result
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                    // Check if the DISPLAY_NAME column exists
                    if (displayNameIndex >= 0) {
                        String contactName = cursor.getString(displayNameIndex);
                        // Add the selected contact to the list
                        ((ArrayAdapter<String>) contactListView.getAdapter()).add(contactName);
                    } else {
                        // Handle the case where DISPLAY_NAME is not available
                        showToast("Display name not available for this contact");
                    }
                }
            }
        }
    }

    public void saveActivationState(boolean activated) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_ACTIVATED, activated);
        editor.apply();
    }

    public void checkForAlerts(ArrayList<String> selectedContacts, boolean userRespondedThroughNotification) {
        // Retrieve the last notification timestamp from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long lastNotificationTimestamp = preferences.getLong(KEY_LAST_NOTIFICATION_TIMESTAMP, 0);

        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Calculate the time elapsed since the last notification
        long timeElapsed = currentTime - lastNotificationTimestamp;

        // Check if the time elapsed is within the allowed response time
        if (userRespondedThroughNotification || (timeElapsed > 0 && timeElapsed < NOTIFICATION_RESPONSE_TIME)) {
            // The user either responded through the notification or within the allowed time
            showToast("User responded within the allowed time or through the notification");

            // Handle the response (replace with your logic)
        } else {
            // The user did not respond to the last notification within the allowed time
            sendAlertSMS(selectedContacts);
        }
    }
    private void saveNotificationTimestamp() {
        // Save the current time as the notification timestamp in SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putLong(KEY_LAST_NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }
    private void sendAlertSMS(ArrayList<String> selectedContacts) {
        // Iterate through the selected contacts and extract phone numbers
        for (String contactName : selectedContacts) {
            String phoneNumber = getPhoneNumber(contactName);

            // Check if the extracted phone number is valid
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                // Send SMS using the extracted phone number
                sendSmsWithPermissionCheck(phoneNumber);
            } else {
                // Handle the case where the phone number is not available
                showToast("Phone number not available for contact: " + contactName);
            }
        }
    }
    private String getPhoneNumber(String contactName) {
        String phoneNumber = null;

        // Define the projection for the query
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

        // Specify the where clause to find the contact by display name
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {contactName};

        // Query the Contacts database
        try (Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null)) {

            // Check if the cursor has results
            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve the phone number
                int phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phoneNumber = cursor.getString(phoneNumberColumnIndex);
            }
        }

        // Return the retrieved phone number (or null if not found)
        return phoneNumber;
    }
    private void sendSMS(String phoneNumber) {
        String message = "Alert: User has not responded to safety check-in notification.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            // Log success
            Log.d("SMS", "Test SMS sent successfully");

            // Show a success message
            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            // Log error
            Log.e("SMS", "Error sending SMS: " + e.getMessage());

            // Show an error message
            Toast.makeText(this, "Error sending SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestSmsPermission(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed to send SMS
            sendSMS(phoneNumber);
        }
    }

    private void sendSmsWithPermissionCheck(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            requestSmsPermission(phoneNumber);
        } else {
            // Permission already granted, proceed to send SMS
            sendSMS(phoneNumber);
        }
    }
}