package com.example.problemsinhouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PostDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PostDB.db";
    private static final int DATABASE_VERSION = 1;

    // Πίνακας posts
    private static final String TABLE_POSTS = "posts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_IMAGE_PATH = "imagePath";


    public PostDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Δημιουργία πίνακα
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_POSTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT)";

        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }

    // Εισαγωγή νέου post
    public boolean insertPost(String username, String title, String content, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        long result = db.insert(TABLE_POSTS, null, values);
        return result != -1;
    }


    // Ανάκτηση όλων των posts (π.χ. για εμφάνιση σε λίστα)
    public List<Post> getAllPosts() {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, title, content, imagePath FROM posts", null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                String title = cursor.getString(1);
                String content = cursor.getString(2);
                String imagePath = cursor.getString(3);

                Post post = new Post(username, title, content, imagePath);
                postList.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return postList;
    }
}
