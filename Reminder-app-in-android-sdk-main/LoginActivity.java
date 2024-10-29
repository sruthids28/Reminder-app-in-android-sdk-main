package com.example.reminderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderapp.R;
import com.example.reminderapp.database.DatabaseHelper;
import com.example.reminderapp.utils.SessionManager;

/**
 * LoginActivity handles user login.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView btnRegister;
    private ProgressBar progressBar;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize DatabaseHelper and SessionManager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Initialize views
        inputEmail = findViewById(R.id.editTextEmailLogin);
        inputPassword = findViewById(R.id.editTextPasswordLogin);
        btnLogin = findViewById(R.id.buttonLogin);
        btnRegister = findViewById(R.id.textViewRegister);
        progressBar = findViewById(R.id.progressBarLogin);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Set click listener for Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
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

                progressBar.setVisibility(View.VISIBLE);

                // Check user credentials
                boolean isValid = dbHelper.checkUser(email, password);
                progressBar.setVisibility(View.GONE);

                if (isValid) {
                    // Retrieve user ID
                    int userId = dbHelper.getUserId(email);

                    // Create user session
                    sessionManager.createLoginSession(userId, email);

                    // Redirect to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for Register TextView
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
