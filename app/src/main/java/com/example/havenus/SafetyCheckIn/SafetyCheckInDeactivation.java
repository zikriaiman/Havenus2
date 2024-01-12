package com.example.havenus.SafetyCheckIn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.havenus.R;

public class SafetyCheckInDeactivation extends AppCompatActivity {

    public static final String PREFS_NAME = "SafetyCheckInPrefs";
    public static final String KEY_ACTIVATED = "activated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_check_in_deactivation);

        Button btnDeactivate = findViewById(R.id.btnDeactivate);

        btnDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deactivate the feature
                saveActivationState(false);

                // Navigate back to the main activity
                Intent intent = new Intent(SafetyCheckInDeactivation.this, SafetyCheckIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
    private void saveActivationState(boolean activated) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_ACTIVATED, activated);
        editor.apply();
        if (!activated) {
            SafetyCheckInActivation.getSelectedContacts().clear();
        }
    }

}