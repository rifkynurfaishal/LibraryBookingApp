package com.androidtask.librarybookingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReturnActivity extends AppCompatActivity {

    ListView listBorrowedBooks;
    Button btnReturn;
    ImageButton btnBackReturn;

    DatabaseHelper db;
    String username;
    ArrayList<String> bookCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        db = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        listBorrowedBooks = findViewById(R.id.listBorrowedBooks);
        btnReturn = findViewById(R.id.btnReturn);
        btnBackReturn = findViewById(R.id.btnBackReturn);

        btnBackReturn.setOnClickListener(v -> finish());

        loadBorrowedBooks();

        btnReturn.setOnClickListener(v -> {
            if (bookCodes.isEmpty()) {
                Toast.makeText(this, "There are no books currently on loan.", Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = listBorrowedBooks.getCheckedItemPosition();
            if (pos == ListView.INVALID_POSITION || pos >= bookCodes.size()) {
                Toast.makeText(this, "Select the book to be returned.", Toast.LENGTH_SHORT).show();
                return;
            }

            String code = bookCodes.get(pos);
            boolean ok = db.returnBook(code, username);
            if (ok) {
                new AlertDialog.Builder(this)
                        .setTitle("Successful Return")
                        .setMessage("Book with code " + code +
                                " returned successfully.\n\nPlease confirm with the library staff.")
                        .setPositiveButton("OK", (d, w) -> d.dismiss())
                        .show();

                loadBorrowedBooks();
            } else {
                Toast.makeText(this, "Failed to return the book.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBorrowedBooks() {
        Cursor c = db.getBorrowedByUser(username);
        ArrayList<String> display = new ArrayList<>();
        bookCodes.clear();

        while (c.moveToNext()) {
            String code = c.getString(0);
            String name = c.getString(1);

            bookCodes.add(code);
            display.add("Code: " + code + "\nName: " + (name != null ? name : "-"));
        }
        c.close();

        if (display.isEmpty()) {
            display.add("No books currently borrowed");
            listBorrowedBooks.setEnabled(false);
            btnReturn.setEnabled(false);
        } else {
            listBorrowedBooks.setEnabled(true);
            btnReturn.setEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_single_choice,
                display
        );
        listBorrowedBooks.setAdapter(adapter);
        listBorrowedBooks.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}
