package com.androidtask.librarybookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {

    ListView listViewBooks;
    ImageButton btnBackBookList;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        listViewBooks = findViewById(R.id.listViewBooks);
        btnBackBookList = findViewById(R.id.btnBackBookList);
        db = new DatabaseHelper(this);

        btnBackBookList.setOnClickListener(v -> finish());

        showAvailableBooks();
    }

    private void showAvailableBooks() {
        Cursor c = db.getAvailableBooks();
        ArrayList<String> books = new ArrayList<>();

        while (c.moveToNext()) {
            String code = c.getString(0);
            String name = c.getString(1);
            books.add("Code : " + code + "\nName : " + name);
        }
        c.close();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.item_card_list, R.id.tvItemText, books);
        listViewBooks.setAdapter(adapter);
    }
}
