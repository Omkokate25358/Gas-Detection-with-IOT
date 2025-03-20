package com.example.gasdetection;

import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Home_Activity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference gasValueRef;

    private TextView gasValueText;
    private TextView gasLevelIndicator;
    private CardView gasCardView;
    private TextView lastUpdatedText;
    private ConstraintLayout mainLayout;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI components
        gasValueText = findViewById(R.id.gasValueText);
        gasLevelIndicator = findViewById(R.id.gasLevelIndicator);
        gasCardView = findViewById(R.id.gasCardView);
        lastUpdatedText = findViewById(R.id.lastUpdatedText);
        mainLayout = findViewById(R.id.mainLayout);
        statusText = findViewById(R.id.statusText);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        gasValueRef = database.getReference("sensor/randomValue");

        // Set initial status
        statusText.setText("Connecting to database...");

        // Listen for gas value changes
        gasValueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer gasValue = snapshot.getValue(Integer.class);
                if (gasValue != null) {
                    // Update UI with new gas value
                    updateGasDisplay(gasValue);

                    // Update last updated time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String currentTime = dateFormat.format(new Date());
                    lastUpdatedText.setText("Last updated: " + currentTime);

                    // Animate the card
                    Animation pulse = AnimationUtils.loadAnimation(Home_Activity.this, R.anim.pulse);
                    gasCardView.startAnimation(pulse);

                    // Update status
                    statusText.setText("Connected to database");
                    statusText.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                gasValueText.setText("Error: " + error.getMessage());
                statusText.setText("Connection error");
                statusText.setTextColor(Color.RED);
            }
        });
    }

    private void updateGasDisplay(int gasValue) {
        // Update gas value text
        gasValueText.setText(gasValue + " ppm");

        // Update gas level indicator and colors based on value
        if (gasValue < 300) {
            gasLevelIndicator.setText("NORMAL");
            gasLevelIndicator.setTextColor(Color.GREEN);
            gasCardView.setCardBackgroundColor(getResources().getColor(R.color.colorSafe));
        } else if (gasValue < 500) {
            gasLevelIndicator.setText("MODERATE");
            gasLevelIndicator.setTextColor(Color.YELLOW);
            gasCardView.setCardBackgroundColor(getResources().getColor(R.color.colorWarning));
        } else {
            gasLevelIndicator.setText("DANGER");
            gasLevelIndicator.setTextColor(Color.RED);
            gasCardView.setCardBackgroundColor(getResources().getColor(R.color.colorDanger));
        }
    }
}