package com.androidtask.librarybookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin;
    TextView tvCreateAccount;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);

        db = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill in username and password!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean valid = db.checkUser(user, pass);

            if (valid) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, LandingActivity.class);
                i.putExtra("username", user);
                startActivity(i);
            } else {
                Toast.makeText(this, "Wrong username or password!", Toast.LENGTH_SHORT).show();
            }
        });

        tvCreateAccount.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });
    }
}
