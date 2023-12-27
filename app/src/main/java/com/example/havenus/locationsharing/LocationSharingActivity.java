package com.example.havenus.locationsharing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.havenus.R;

public class LocationSharingActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int CONTACT_PICKER_REQUEST_CODE = 1002;
    private double latitude;
    private double longitude;
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
    }

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
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();

                // Replace this with your logic to send the location to selected contacts
                sendLocationViaSMS(latitude, longitude);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendLocationViaSMS(double latitude, double longitude) {
        // Compose a message with the current location
        String message = "My current location is: " + latitude + ", " + longitude;

        // Replace with your logic to choose contacts or open the contact picker
        openContactPicker(message);
    }

    private void openContactPicker(String message) {
        // Open the contact picker or use your own logic to select contacts
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_REQUEST_CODE);
    }

    // Handle the result of the contact picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            // Extract contact information and send the SMS
            sendSMS(contactUri, "My current location is: " + latitude + ", " + longitude);
        }
    }

    private void sendSMS(Uri contactUri, String message) {
        // Use the contact information to send an SMS
        // Extract contact details (name, phone number) from the contactUri
        // Use SmsManager to send the SMS
        // Example:
        // SmsManager smsManager = SmsManager.getDefault();
        // smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        // Show a success message
        Toast.makeText(this, "Location sent via SMS", Toast.LENGTH_SHORT).show();
    }
}