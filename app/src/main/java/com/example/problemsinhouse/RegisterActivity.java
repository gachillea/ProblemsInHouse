package com.example.problemsinhouse;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button registerButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // θα το φτιάξουμε αμέσως μετά

        db = new DatabaseHelper(this);

        usernameInput = findViewById(R.id.editTextUsername);
        passwordInput = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.fillAllFields) , Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.userExists(username)) {
                Toast.makeText(this, getString(R.string.userAlreadyExists), Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.insertUser(username, password);

            if (success) {
                Toast.makeText(this, getString(R.string.registerSuccess), Toast.LENGTH_SHORT).show();
                finish(); // Επιστροφή στο login
            } else {
                Toast.makeText(this, getString(R.string.registerFail), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

