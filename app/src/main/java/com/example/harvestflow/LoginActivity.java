package com.example.harvestflow;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.harvestflow.Database.DatabaseHelper;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    private EditText username, password;
    private TextView greetingText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setGreeting();
        setupLoginButton();
    }

    private void initializeViews() {
        dbHelper = new DatabaseHelper(this);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        greetingText = findViewById(R.id.greetingText);
    }

    private void setGreeting() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting = "Good Evening";
        } else {
            greeting = "Good Night";
        }

        greetingText.setText(greeting);
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (validateInputs(user, pass)) {
            performLogin(user, pass);
        }
    }

    private boolean validateInputs(String user, String pass) {
        if (user.isEmpty()) {
            username.setError("Username is required");
            username.requestFocus();
            return false;
        }

        if (pass.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        }

        return true;
    }

    private void performLogin(String user, String pass) {
        // Check if user is Admin
        if (dbHelper.validateAdmin(user, pass)) {
            navigateToActivity(AdminPanelActivity.class, user, "Welcome Admin!");
        }
        // Check if user is Collector
        else if (dbHelper.validateCollector(user, pass)) {
            navigateToActivity(CollectorDashboardActivity.class, user, "Welcome Collector!");
        }
        // Invalid login
        else {
            showLoginError();
        }
    }

    private void navigateToActivity(Class<?> destinationActivity, String username, String message) {
        Intent intent = new Intent(LoginActivity.this, destinationActivity);
        intent.putExtra("username", username);
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    private void showLoginError() {
        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        password.setText("");  // Clear password field for security
    }

    @Override
    public void onBackPressed() {
        // Handle back button press - maybe show exit confirmation dialog
        super.onBackPressed();
    }
}