package com.stevedao.note.model;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by thanh.dao on 06/04/2016.
 *
 */
public class DatabaseSpec {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "MDNote.db";

    public static class NoteDB {
        public static final String TABLE_NAME = "note";

        public static final String FIELD_PKEY = "_id";
        public static final String FIELD_TITLE = "title";
        public static final String FIELD_COLOR = "color";
        public static final String FIELD_IS_DONE = "isDone";
        public static final String FIELD_STORAGE_MODE = "storageMode";
        public static final String FIELD_LAST_MODIFIED = "lastModified";
        public static final String FIELD_DELETED_TIME = "deletedTime";

        public static final String TABLE_CREATION_COMMAND = "CREATE TABLE " + NoteDB.TABLE_NAME + " ("
                + NoteDB.FIELD_PKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteDB.FIELD_TITLE + " TEXT NOT NULL, "
                + NoteDB.FIELD_COLOR + " INTEGER NOT NULL, "
                + NoteDB.FIELD_IS_DONE + " INTEGER NOT NULL, "
                + NoteDB.FIELD_STORAGE_MODE + " INTEGER NOT NULL, "
                + NoteDB.FIELD_LAST_MODIFIED + " INTEGER NOT NULL, "
                + NoteDB.FIELD_DELETED_TIME + " INTEGER"
                + " );";

        public static final String TABLE_DROP_COMMAND =  "DROP TABLE IF EXISTS " + NoteDB.TABLE_NAME;

        public static void onCreate(SQLiteDatabase database) {
            if (database == null) {
                return;
            }

            database.execSQL(TABLE_CREATION_COMMAND);
        }

//        @SuppressWarnings("unused")
//        public static void onUpgrade(SQLiteDatabase database, int updateVersion) {
//            if (database == null) {
//                return;
//            }

            // Comment for later use
            //            switch (updateVersion) {
            //                case 1:
            //                    break;
            //                default:
            //                    break;
            //            }
//        }

        @SuppressWarnings("unused")
        public static void dropTable(SQLiteDatabase database) {
            if (database == null) {
                return;
            }

            database.execSQL(TABLE_DROP_COMMAND);
        }
    }

    public static class ItemDB {
        public static final String TABLE_NAME = "Item";

        public static final String FIELD_PKEY = "_id";
        public static final String FIELD_NOTE_ID = "noteId";
        public static final String FIELD_CONTENT = "content";
        public static final String FIELD_CHECKED = "checked";
        public static final String FIELD_INDEX = "content_index";

        public static final String TABLE_CREATION_COMMAND = "CREATE TABLE " + ItemDB.TABLE_NAME + " ("
                + ItemDB.FIELD_PKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemDB.FIELD_NOTE_ID + " INTEGER NOT NULL, "
                + ItemDB.FIELD_CONTENT + " TEXT NOT NULL, "
                + ItemDB.FIELD_CHECKED + " INTEGER NOT NULL, "
                + ItemDB.FIELD_INDEX + " INTEGER NOT NULL"
                + " );";

        public static final String TABLE_DROP_COMMAND = "DROP TABLE IF EXISTS " + ItemDB.TABLE_NAME;

        public static void onCreate(SQLiteDatabase database) {
            if (database == null) {
                return;
            }

            database.execSQL(TABLE_CREATION_COMMAND);
        }

//        @SuppressWarnings("unused")
//        public static void onUpgrade(SQLiteDatabase database, int updateVersion) {
//            if (database == null) {
//                return;
//            }

            // Comment for later use
//            switch (updateVersion) {
//                case 1:
//                    break;
//                default:
//                    break;
//            }
//        }

        @SuppressWarnings("unused")
        public static void dropTable(SQLiteDatabase database) {
            if (database == null) {
                return;
            }

            database.execSQL(TABLE_DROP_COMMAND);
        }
    }
}
