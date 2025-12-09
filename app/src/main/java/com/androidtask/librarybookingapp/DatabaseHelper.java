package com.androidtask.librarybookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "library.db";

    public static final String TABLE_BORROW = "borrow_table";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_BOOKS = "books_table";

    // Naik versi untuk rebuild DB
    public static final int DB_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Master buku
        db.execSQL("CREATE TABLE " + TABLE_BOOKS +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT UNIQUE, name TEXT)");

        // Peminjaman (returned: 0 = dipinjam, 1 = dikembalikan)
        db.execSQL("CREATE TABLE " + TABLE_BORROW +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bookid TEXT, username TEXT, duration TEXT, returned INTEGER DEFAULT 0)");

        // Users
        db.execSQL("CREATE TABLE " + TABLE_USERS +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, password TEXT)");

        // 1 user default
        ContentValues cvUser = new ContentValues();
        cvUser.put("username", "admin");
        cvUser.put("password", "admin123");
        db.insert(TABLE_USERS, null, cvUser);

        // Seed 10 buku
        insertDefaultBooks(db);
    }

    private void insertDefaultBooks(SQLiteDatabase db) {
        insertBook(db, "BK001", "Pemrograman Java Dasar");
        insertBook(db, "BK002", "Pemrograman Android");
        insertBook(db, "BK003", "Basis Data dengan SQLite");
        insertBook(db, "BK004", "Algoritma dan Struktur Data");
        insertBook(db, "BK005", "Jaringan Komputer");
        insertBook(db, "BK006", "Sistem Operasi");
        insertBook(db, "BK007", "Pemrograman Web Dasar");
        insertBook(db, "BK008", "Kecerdasan Buatan");
        insertBook(db, "BK009", "Machine Learning Dasar");
        insertBook(db, "BK010", "Analisis & Perancangan Sistem Informasi");
    }

    private void insertBook(SQLiteDatabase db, String code, String name) {
        ContentValues cv = new ContentValues();
        cv.put("code", code);
        cv.put("name", name);
        db.insert(TABLE_BOOKS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BORROW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    // =========================
    // BORROW
    // =========================

    public boolean insertBorrow(String bookid, String username, String duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("bookid", bookid);
        cv.put("username", username);
        cv.put("duration", duration);
        cv.put("returned", 0);

        long result = db.insert(TABLE_BORROW, null, cv);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BORROW, null);
    }

    public Cursor getBorrowedByUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql =
                "SELECT DISTINCT br.bookid, b.name " +
                        "FROM " + TABLE_BORROW + " br " +
                        "LEFT JOIN " + TABLE_BOOKS + " b ON br.bookid = b.code " +
                        "WHERE br.username=? AND br.returned=0";

        return db.rawQuery(sql, new String[]{username});
    }

    public boolean returnBook(String bookCode, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("returned", 1);

        int rows = db.update(
                TABLE_BORROW,
                cv,
                "bookid=? AND username=? AND returned=0",
                new String[]{bookCode, username}
        );

        return rows > 0;
    }

    // =========================
    // BOOKS
    // =========================

    // Buku yang masih available (tidak sedang dipinjam siapa pun)
    public Cursor getAvailableBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql =
                "SELECT b.code, b.name FROM " + TABLE_BOOKS + " b " +
                        "LEFT JOIN " + TABLE_BORROW + " br " +
                        "ON b.code = br.bookid AND br.returned = 0 " +
                        "WHERE br.ID IS NULL";
        return db.rawQuery(sql, null);
    }

    // =========================
    // USERS
    // =========================

    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("username", username);
        cv.put("password", password);

        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?",
                new String[]{username, password}
        );

        boolean ada = cursor.getCount() > 0;
        cursor.close();
        return ada;
    }
}
