package com.example.havenus.MenstrualHealthGame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.havenus.R;

public class MenstrualHealthGameResult extends AppCompatActivity {
    private TextView scoreTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menstrual_health_game_result);

        // Initialize your TextView
        scoreTextView = findViewById(R.id.scoreNumber);

        // Retrieve the score from the intent
        int score = getIntent().getIntExtra("SCORE_EXTRA", 0);

        // Update the TextView with the score
        scoreTextView.setText(score + "/10");

        Button exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main activity
                Intent intent = new Intent(MenstrualHealthGameResult.this, MenstrualHealthGame.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}
