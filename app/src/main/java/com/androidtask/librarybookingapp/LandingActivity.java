package com.androidtask.librarybookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LandingActivity extends AppCompatActivity {

    LinearLayout btnCardDaftarPinjam, btnCardBooking, btnCardDaftarBuku, btnCardPengembalian;
    Button btnLogout;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        username = getIntent().getStringExtra("username");

        btnCardDaftarPinjam = findViewById(R.id.btnCardDaftarPinjam);
        btnCardBooking = findViewById(R.id.btnCardBooking);
        btnCardDaftarBuku = findViewById(R.id.btnCardDaftarBuku);
        btnCardPengembalian = findViewById(R.id.btnCardPengembalian);
        btnLogout = findViewById(R.id.btnLogout);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("Welcome, " + username + "!");
        }

        // Daftar Pinjam -> ListActivity
        btnCardDaftarPinjam.setOnClickListener(v -> {
            Intent i = new Intent(LandingActivity.this, ListActivity.class);
            startActivity(i);
        });

        // Booking -> BorrowActivity
        btnCardBooking.setOnClickListener(v -> {
            Intent i = new Intent(LandingActivity.this, BorrowActivity.class);
            i.putExtra("username", username);
            startActivity(i);
        });

        // Daftar Buku -> BookListActivity
        btnCardDaftarBuku.setOnClickListener(v -> {
            Intent i = new Intent(LandingActivity.this, BookListActivity.class);
            startActivity(i);
        });

        // Pengembalian Buku -> ReturnActivity
        btnCardPengembalian.setOnClickListener(v -> {
            Intent i = new Intent(LandingActivity.this, ReturnActivity.class);
            i.putExtra("username", username);
            startActivity(i);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(LandingActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }
}
