package com.androidtask.librarybookingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper db;
    ImageButton btnBackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listViewData);
        btnBackList = findViewById(R.id.btnBackList);
        db = new DatabaseHelper(this);

        btnBackList.setOnClickListener(v -> finish());

        showData();

        // Klik item -> popup konfirmasi
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String detail = (String) parent.getItemAtPosition(position);

            new AlertDialog.Builder(this)
                    .setTitle("Loan Details")
                    .setMessage(detail + "\n\nPlease confirm with the library staff.")
                    .setPositiveButton("OK", (d, w) -> d.dismiss())
                    .show();
        });
    }

    private void showData() {
        Cursor c = db.getAllData();
        ArrayList<String> data = new ArrayList<>();

        while (c.moveToNext()) {
            String bookId = c.getString(1);
            String user = c.getString(2);
            String duration = c.getString(3);
            int returnedInt = c.getInt(4);
            String status = (returnedInt == 1) ? "Returned" : "Borrowed";

            data.add(
                    "Book ID : " + bookId +
                            "\nUser : " + user +
                            "\nDuration : " + duration +
                            "\nStatus : " + status
            );
        }
        c.close();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.item_card_list, R.id.tvItemText, data);
        listView.setAdapter(adapter);
    }
}
