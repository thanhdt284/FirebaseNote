package com.stevedao.note.model;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by thanh.dao on 06/04/2016.
 *
 */
public class DatabaseSpec {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Note.db";

    public static class NoteDB {
        public static final String TABLE_NAME = "note";

        public static final String FIELD_PKEY = "_id";
        public static final String FIELD_TITLE = "title";
        public static final String FIELD_COLOR = "color";
        public static final String FIELD_IS_DONE = "isDone";
        public static final String FIELD_STORAGE_MODE = "storageMode";
        public static final String FIELD_LAST_MODIFIED = "lastModified";
        public static final String FIELD_DELETED_TIME = "deletedTime";

        public static final String TABLE_CREATION_COMMAND = "CREATE TABLE " + TABLE_NAME + " ("
                + FIELD_PKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_TITLE + " TEXT NOT NULL, "
                + FIELD_COLOR + " INTEGER NOT NULL, "
                + FIELD_IS_DONE + " INTEGER NOT NULL, "
                + FIELD_STORAGE_MODE + " INTEGER NOT NULL, "
                + FIELD_LAST_MODIFIED + " INTEGER NOT NULL, "
                + FIELD_DELETED_TIME + " INTEGER"
                + " );";

        public static final String TABLE_DROP_COMMAND =  "DROP TABLE IF EXISTS " + TABLE_NAME;

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

        public static final String TABLE_CREATION_COMMAND = "CREATE TABLE " + TABLE_NAME + " ("
                + FIELD_PKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_NOTE_ID + " INTEGER NOT NULL, "
                + FIELD_CONTENT + " TEXT NOT NULL, "
                + FIELD_CHECKED + " INTEGER NOT NULL, "
                + FIELD_INDEX + " INTEGER NOT NULL"
                + " );";

        public static final String TABLE_DROP_COMMAND = "DROP TABLE IF EXISTS " + TABLE_NAME;

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

    public static class UserDB {
        public static final String TABLE_NAME = "user";

        public static final String FIELD_PKEY = "id";
        public static final String FIELD_DISPLAY_NAME = "displayName";
        public static final String FIELD_EMAIL = "email";
        public static final String FIELD_PHOTO_URL = "photoURL";

        public static final String TABLE_CREATION_COMMAND = "CREATE TABLE " + TABLE_NAME + " ("
                + FIELD_PKEY + " TEXT NOT NULL, "
                + FIELD_DISPLAY_NAME + " TEXT NOT NULL, "
                + FIELD_EMAIL + " TEXT NOT NULL, "
                + FIELD_PHOTO_URL + " TEXT "
                + " );";

        public static final String TABLE_DROP_COMMAND = "DROP TABLE IF EXISTS " + TABLE_NAME;

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
