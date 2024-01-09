package com.example.havenus.locationsharing;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.havenus.R;
public class LocationSharingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_sharing);

        ImageButton btnSendLocation = findViewById(R.id.sms);
        btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();
            }
        });

        ImageButton btnSendLocation1 = findViewById(R.id.whatsapp);
        btnSendLocation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLiveLocationViaWhatsApp();
            }
        });

        ImageButton btnSendLocation2 = findViewById(R.id.instagram);
        btnSendLocation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLiveLocationViaInstagram();
            }
        });

        ImageButton btnSendLocation3 = findViewById(R.id.telegram);
        btnSendLocation3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLiveLocationViaTelegram();
            }
        });

        ImageButton btnSendLocation4 = findViewById(R.id.snapchat);
        btnSendLocation4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLiveLocationViaSnapchat();
            }
        });

        ImageButton btnSendLocation5 = findViewById(R.id.more);
        btnSendLocation5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLiveLocationViaOthers();
            }
        });
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int CONTACT_PICKER_REQUEST_CODE = 1002;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1003;
    private static final int CONTACT_PICKER_REQUEST_CODE_WHATSAPP = 1004;
    private static final int CONTACT_PICKER_REQUEST_CODE_FAVORITES = 1005;
    private double latitude;
    private double longitude;

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            sendLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location lastKnownLocation = (lastKnownLocationGPS != null) ? lastKnownLocationGPS : lastKnownLocationNetwork;

            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                sendLocationViaSMS(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendLocationViaSMS(double latitude, double longitude) {
        // Compose a message with the current location
        String message = "My current location is: " + latitude + ", " + longitude;

        openContactPicker();
    }

    private void openContactPicker() {
        // Open the contact picker or use your own logic to select contacts
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            // Extract contact information and send the SMS
            if (contactUri != null) {
                String phoneNumber = retrieveContactNumber(contactUri);
                if (phoneNumber != null) {
                    sendSmsWithPermissionCheck(phoneNumber);
                    // Show a success message
                    Toast.makeText(this, "Location sent via SMS", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to retrieve contact information", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private String retrieveContactNumber(Uri contactUri) {
        String phoneNumber = null;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

        try {
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phoneNumber = cursor.getString(numberIndex);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

    private void sendSMS(String phoneNumber) {
        String message = "Check my live location: https://maps.google.com/?q=" + latitude + "," + longitude;

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
    private void sendLiveLocationViaWhatsApp() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location lastKnownLocation = (lastKnownLocationGPS != null) ? lastKnownLocationGPS : lastKnownLocationNetwork;

            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                sendWhatsAppMessage(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendWhatsAppMessage(double latitude, double longitude) {
        // Compose a message with a Google Maps link (this link will show your live location)
        String message = "Check my live location: https://maps.google.com/?q=" + latitude + "," + longitude;

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendLiveLocationViaInstagram() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location lastKnownLocation = (lastKnownLocationGPS != null) ? lastKnownLocationGPS : lastKnownLocationNetwork;

            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                sendInstagramMessage(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendInstagramMessage(double latitude, double longitude) {
        String message = "Check my live location: https://maps.google.com/?q=" + latitude + "," + longitude;

        // Create an intent to open Instagram's direct message activity
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setPackage("com.instagram.android");

        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Instagram not installed", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendLiveLocationViaTelegram() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location lastKnownLocation = (lastKnownLocationGPS != null) ? lastKnownLocationGPS : lastKnownLocationNetwork;

            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                sendTelegramMessage(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendTelegramMessage(double latitude, double longitude) {
        String message = "Check my live location: https://maps.google.com/?q=" + latitude + "," + longitude;

        // Create an intent to open Telegram's share/send message activity
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setPackage("org.telegram.messenger");

        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Telegram not installed", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendLiveLocationViaSnapchat() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location lastKnownLocation = (lastKnownLocationGPS != null) ? lastKnownLocationGPS : lastKnownLocationNetwork;

            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                sendSnapchatMessage(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendSnapchatMessage(double latitude, double longitude) {
        String message = "Check my live location: https://maps.google.com/?q=" + latitude + "," + longitude;

        // Create an intent to open Snapchat's share/send message activity
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setPackage("com.snapchat.android");

        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Snapchat not installed", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendLiveLocationViaOthers() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location lastKnownLocation = (lastKnownLocationGPS != null) ? lastKnownLocationGPS : lastKnownLocationNetwork;

            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();

                openMoreOptions(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openMoreOptions(double latitude, double longitude) {
        String message = "Check my live location: https://maps.google.com/?q=" + latitude + "," + longitude;

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);

        // Create a chooser to let the user pick a messaging app
        Intent chooserIntent = Intent.createChooser(sendIntent, "Share via");

        try {
            startActivity(chooserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No messaging apps installed", Toast.LENGTH_SHORT).show();
        }
    }
}