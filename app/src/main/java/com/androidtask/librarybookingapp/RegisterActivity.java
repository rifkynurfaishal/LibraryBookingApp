package com.androidtask.librarybookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText etNewUser, etNewPass, etConfirmPass;
    Button btnCreateAccount;
    TextView tvBackToLogin;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        etNewUser      = findViewById(R.id.etNewUser);
        etNewPass      = findViewById(R.id.etNewPass);
        etConfirmPass  = findViewById(R.id.etConfirmPass);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvBackToLogin    = findViewById(R.id.tvBackToLogin);

        // Tombol buat akun
        btnCreateAccount.setOnClickListener(v -> createAccount());

        // Text "Already have account? Login"
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void createAccount() {
        String user = etNewUser.getText().toString().trim();
        String pass = etNewPass.getText().toString().trim();
        String confirm = etConfirmPass.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Password and confirmation are not the same.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 4) {
            Toast.makeText(this, "Password must be at least 4 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        // insertUser sudah akan gagal kalau username sudah ada karena UNIQUE
        boolean inserted = db.insertUser(user, pass);

        if (inserted) {
            Toast.makeText(this,
                    "Account created successfully. Please login.",
                    Toast.LENGTH_LONG).show();
            finish(); // kembali ke halaman login
        } else {
            Toast.makeText(this,
                    "Username already used. Please choose another.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
