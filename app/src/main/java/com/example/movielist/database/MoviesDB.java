package com.example.movielist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MoviesDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "movies.db";
    public static final int VERSION = 1;

    public MoviesDB(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MoviesTableHelper.CREATE);
        db.execSQL(FavoritiTableHelper.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
