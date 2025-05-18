package com.example.problemsinhouse;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button registerButton;
    private FirestoreHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // θα το φτιάξουμε αμέσως μετά


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

            FirestoreHelper.userExists(username, success->{if (success) {
                Toast.makeText(this, getString(R.string.userAlreadyExists), Toast.LENGTH_SHORT).show();
                return;
            }});

            FirestoreHelper.insertUser(username, password, success->{if (success) {
                Toast.makeText(this, getString(R.string.registerSuccess), Toast.LENGTH_SHORT).show();
                finish(); // Επιστροφή στο login
            } else {
                Toast.makeText(this, getString(R.string.registerFail), Toast.LENGTH_SHORT).show();
            }});
        });

    }
}

