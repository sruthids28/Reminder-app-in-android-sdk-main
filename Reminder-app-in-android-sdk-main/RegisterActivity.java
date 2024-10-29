package com.example.reminderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderapp.R;
import com.example.reminderapp.database.DatabaseHelper;
import com.example.reminderapp.utils.SessionManager;

/**
 * RegisterActivity handles user registration.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnRegister, btnBackToLogin;
    private ProgressBar progressBar;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize DatabaseHelper and SessionManager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Initialize views
        inputEmail = findViewById(R.id.editTextEmailRegister);
        inputPassword = findViewById(R.id.editTextPasswordRegister);
        btnRegister = findViewById(R.id.buttonRegister);
        btnBackToLogin = findViewById(R.id.buttonBackToLogin);
        progressBar = findViewById(R.id.progressBarRegister);

        // Set click listener for Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    inputPassword.setError("Password must be at least 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Register user in the database
                long userId = dbHelper.registerUser(email, password);
                progressBar.setVisibility(View.GONE);

                if (userId != -1) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    // Create user session
                    sessionManager.createLoginSession((int) userId, email);

                    // Redirect to MainActivity
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for Back to Login button
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
