package com.example.gasdetection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView register;

    private EditText emailField, passwordField;
    private Button btnLogin;

    private ProgressBar progressBar;
    private FirebaseAuth auth;

    // for one time login
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // all data members
        register=findViewById(R.id.tvRegister);
        auth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);


        // check for already login
//        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
//            startActivity(new Intent(LoginActivity.this, Home_Activity.class));
//            finish();
//        }



        // button functionality
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,Register_Activity.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(v -> loginUser());


    }

    private void loginUser() {

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
//                    saveLoginSession();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, Home_Activity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void saveLoginSession() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("isLoggedIn", true);
//        editor.apply();
//    }
}