package com.androidtask.librarybookingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class BorrowActivity extends AppCompatActivity {

    Spinner spBook;
    EditText etDuration;
    Button btnSubmit, btnView, btnViewBooks;
    ImageButton btnBackBorrow;

    DatabaseHelper db;
    String username;

    ArrayList<String> bookCodes = new ArrayList<>();
    String selectedBookCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);

        db = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        spBook       = findViewById(R.id.spBook);
        etDuration   = findViewById(R.id.etDuration);
        btnSubmit    = findViewById(R.id.btnSubmit);
        btnView      = findViewById(R.id.btnView);
        btnViewBooks = findViewById(R.id.btnViewBooks);
        btnBackBorrow = findViewById(R.id.btnBackBorrow);

        loadAvailableBooks();

        btnSubmit.setOnClickListener(v -> saveBorrow());

        btnView.setOnClickListener(v ->
                startActivity(new Intent(BorrowActivity.this, ListActivity.class)));

        btnViewBooks.setOnClickListener(v ->
                startActivity(new Intent(BorrowActivity.this, BookListActivity.class)));

        btnBackBorrow.setOnClickListener(v -> finish());
    }

    private void loadAvailableBooks() {
        Cursor c = db.getAvailableBooks();
        ArrayList<String> display = new ArrayList<>();
        bookCodes.clear();

        while (c.moveToNext()) {
            String code = c.getString(0);
            String name = c.getString(1);
            bookCodes.add(code);
            display.add(code + " - " + name);
        }
        c.close();

        if (display.isEmpty()) {
            display.add("No books available");
        }

        ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                display
        );
        bookAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBook.setAdapter(bookAdapter);

        spBook.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       android.view.View view,
                                       int position, long id) {
                if (bookCodes.size() > 0 && position < bookCodes.size()) {
                    selectedBookCode = bookCodes.get(position);
                } else {
                    selectedBookCode = null;
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedBookCode = null;
            }
        });
    }

    private void saveBorrow() {

        if (selectedBookCode == null) {
            Toast.makeText(this, "No book available to borrow.", Toast.LENGTH_SHORT).show();
            return;
        }

        String durationInput = etDuration.getText().toString().trim();
        if (durationInput.isEmpty()) {
            Toast.makeText(this, "Please input duration in days.", Toast.LENGTH_SHORT).show();
            return;
        }

        int days;
        try {
            days = Integer.parseInt(durationInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duration must be a number.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (days < 1 || days > 15) {
            Toast.makeText(this, "Duration must be between 1–15 days.", Toast.LENGTH_SHORT).show();
            return;
        }

        String duration = days + " Days";

        boolean inserted = db.insertBorrow(selectedBookCode, username, duration);

        if (inserted) {
            new AlertDialog.Builder(this)
                    .setTitle("Booking Saved")
                    .setMessage("Your booking has been saved.\n\n" +
                            "Please come to the library and show this booking to the librarian " +
                            "for confirmation.")
                    .setPositiveButton("OK", (d, w) -> d.dismiss())
                    .show();

            // reset input & refresh list book available
            etDuration.setText("");
            loadAvailableBooks();
        } else {
            Toast.makeText(this, "Failed to save booking.", Toast.LENGTH_SHORT).show();
        }
    }
}
