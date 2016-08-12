package com.stevedao.note.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thanh.dao on 06/04/2016.
 *
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static DatabaseOpenHelper mInstance;

    public static synchronized DatabaseOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseOpenHelper(context);
        }

        return mInstance;
    }

    public DatabaseOpenHelper(Context context) {
        super(context, DatabaseSpec.DB_NAME, null, DatabaseSpec.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        DatabaseSpec.NoteDB.dropTable(db);
//        DatabaseSpec.ItemDB.dropTable(db);
        DatabaseSpec.NoteDB.onCreate(db);
        DatabaseSpec.ItemDB.onCreate(db);
    }

    @SuppressWarnings("unused")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db is out of date; update db according to corresponding version
        if (oldVersion < newVersion) {
            while (oldVersion < newVersion) {
                int updateVersion = ++oldVersion;
//                DatabaseSpec.NoteDB.onUpgrade(db, updateVersion);
            }
        }
    }

}
