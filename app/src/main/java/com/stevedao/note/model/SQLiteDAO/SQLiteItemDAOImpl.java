package com.stevedao.note.model.SQLiteDAO;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.EntityDAO;
import com.stevedao.note.model.Item;
import com.stevedao.note.model.Note;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class SQLiteItemDAOImpl implements EntityDAO<Item> {
    private final DatabaseOpenHelper dbHelper;

    public SQLiteItemDAOImpl(Context context) {
        dbHelper = DatabaseOpenHelper.getInstance(context);
    }

    @Override
    public Object addEntity(Item item) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseSpec.ItemDB.FIELD_FIREBASE_KEY, item.getFirebaseId());
            values.put(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY, item.getFirebaseNoteId());
            values.put(DatabaseSpec.ItemDB.FIELD_NOTE_ID, item.getNoteId());
            values.put(DatabaseSpec.ItemDB.FIELD_CONTENT, item.getContent());
            values.put(DatabaseSpec.ItemDB.FIELD_CHECKED, item.isChecked() ? 1 : 0);
            values.put(DatabaseSpec.ItemDB.FIELD_INDEX, item.getIndex());

            int insertedId = (int) db.insert(DatabaseSpec.ItemDB.TABLE_NAME, null, values);
            if (insertedId >= 0) {
                item.setId(insertedId);
            } else {
                Log.e(Common.APPTAG, "SQLiteItemDAOImpl - addEntity: Add item error: insertedId = -1");
            }

            return insertedId;
        }
    }

    @Override
    public int addEntities(ArrayList<Item> entities) {
        synchronized (dbHelper) {
            int count = 0;

            for (Item item : entities) {
                if (((Integer) addEntity(item)) >= 0) {
                    count++;
                }
            }

            Log.e(Common.APPTAG, "SQLiteItemDAOImpl - addEntities: added " + count + " items");
            return count;
        }
    }

    @Override
    public Item getEntity(Object id) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Item item = null;

            String[] projection = { DatabaseSpec.ItemDB.FIELD_PKEY, DatabaseSpec.ItemDB.FIELD_FIREBASE_KEY,
                    DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY, DatabaseSpec.ItemDB.FIELD_NOTE_ID,
                    DatabaseSpec.ItemDB.FIELD_CONTENT, DatabaseSpec.ItemDB.FIELD_CHECKED,
                    DatabaseSpec.ItemDB.FIELD_INDEX };
            String selection = DatabaseSpec.ItemDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            Cursor cursor =
                    db.query(DatabaseSpec.ItemDB.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                item = new Item(cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_PKEY)),
                                cursor.getString(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_FIREBASE_KEY)),
                                cursor.getString(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_NOTE_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_CONTENT)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_CHECKED)) > 0,
                                cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_INDEX)));

                cursor.close();
            }

            return item;
        }
    }

    @Override
    public ArrayList<Item> getAllEntities(@Nullable String column, Object value) {
        synchronized (dbHelper) {
            ArrayList<Item> list = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String[] projection = { DatabaseSpec.ItemDB.FIELD_PKEY, DatabaseSpec.ItemDB.FIELD_FIREBASE_KEY,
                    DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY, DatabaseSpec.ItemDB.FIELD_NOTE_ID,
                    DatabaseSpec.ItemDB.FIELD_CONTENT, DatabaseSpec.ItemDB.FIELD_CHECKED,
                    DatabaseSpec.ItemDB.FIELD_INDEX };

            String selection = null;
            String[] selectionArgs = null;

            if (column != null) {
                if (column.equals(DatabaseSpec.ItemDB.FIELD_NOTE_ID)) {
                    selection = DatabaseSpec.ItemDB.FIELD_NOTE_ID + " = ?";
                } else if (column.equals(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY)) {
                    selection = DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY + " = ?";
                }

                selectionArgs = new String[] { String.valueOf(value) };
            }

            Cursor cursor =
                    db.query(DatabaseSpec.ItemDB.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Item item = new Item(cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_PKEY)),
                                         cursor.getString(
                                                 cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_FIREBASE_KEY)),
                                         cursor.getString(
                                                 cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY)),
                                         cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_NOTE_ID)),
                                         cursor.getString(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_CONTENT)),
                                         cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_CHECKED)) > 0,
                                         cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_INDEX)));
                    list.add(item);
                } while (cursor.moveToNext());

                cursor.close();
            }

            return list;
        }
    }

    @Override
    public void updateEntity(Item item) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseSpec.ItemDB.FIELD_FIREBASE_KEY, item.getFirebaseId());
            values.put(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY, item.getFirebaseNoteId());
            values.put(DatabaseSpec.ItemDB.FIELD_NOTE_ID, item.getNoteId());
            values.put(DatabaseSpec.ItemDB.FIELD_CONTENT, item.getContent());
            values.put(DatabaseSpec.ItemDB.FIELD_CHECKED, item.isChecked() ? 1 : 0);
            values.put(DatabaseSpec.ItemDB.FIELD_INDEX, item.getIndex());

            String selection;
            if (item.getFirebaseNoteId().equals("")) {
                selection = DatabaseSpec.ItemDB.FIELD_PKEY + " = ?";
            } else {
                selection = DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY + " = ?";
            }
            String[] selectionArgs =
                    { String.valueOf(item.getFirebaseNoteId().equals("") ? item.getId() : item.getFirebaseNoteId()) };

            db.update(DatabaseSpec.ItemDB.TABLE_NAME, values, selection, selectionArgs);
        }
    }

    @Override
    public void deleteEntity(Item item) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection;
            if (item.getFirebaseNoteId().equals("")) {
                selection = DatabaseSpec.ItemDB.FIELD_PKEY + " = ?";
            } else {
                selection = DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY + " = ?";
            }
            String[] selectionArgs =
                    { String.valueOf(item.getFirebaseNoteId().equals("") ? item.getId() : item.getFirebaseNoteId()) };

            db.delete(DatabaseSpec.ItemDB.TABLE_NAME, selection, selectionArgs);
        }
    }

    public int deleteItemsByNote(Note note) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = "";
            if (note.getFirebaseId().equals("")) {
                selection = DatabaseSpec.ItemDB.FIELD_NOTE_ID + " = ?";
            } else {
                selection = DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY + " = ?";
            }
            String[] selectionArgs =
                    { String.valueOf(note.getFirebaseId().equals("") ? note.getId() : note.getFirebaseId()) };

            return db.delete(DatabaseSpec.ItemDB.TABLE_NAME, selection, selectionArgs);
        }
    }

    public void deleteAllItemData() {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.delete(DatabaseSpec.ItemDB.TABLE_NAME, null, null);
        }
    }
}
