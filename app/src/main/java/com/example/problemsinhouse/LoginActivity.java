package com.example.problemsinhouse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton ,registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        usernameInput = findViewById(R.id.editTextUsername);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.fillAllFields), Toast.LENGTH_SHORT).show();
                return;
            }

            FirestoreHelper.checkUser(username, password, user ->{if (user != null) {
                Toast.makeText(this, getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                // Πήγαινε σε άλλο activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", user); // Στέλνω τον user
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, getString(R.string.invalidCredentials), Toast.LENGTH_SHORT).show();
            }});

        });

        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}
