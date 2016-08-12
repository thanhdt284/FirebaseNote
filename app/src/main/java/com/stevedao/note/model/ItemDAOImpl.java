package com.stevedao.note.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.stevedao.note.control.Common;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class ItemDAOImpl implements EntityDAO<Item> {
    private static final String TAG = "ItemDaoImpl";
    private Context mContext;

    public ItemDAOImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public String addEntity(Item item) {
        DatabaseReference userRef = FirebaseUtil.getUserRef();
        FirebaseUser mCurrentUser = FirebaseUtil.getCurrentUser();

        User user = new User(mCurrentUser.getEmail(), mCurrentUser.getDisplayName(),
                             mCurrentUser.getPhotoUrl() == null ? "" : mCurrentUser.getPhotoUrl().toString());

        if (userRef != null) {
            userRef.setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.e(TAG, "onComplete: addEntity Error " + databaseError.getMessage());
                    Toast.makeText(mContext, "Adding user failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return FirebaseUtil.getCurrentUserId();
    }

    public void addEntities(ArrayList<Item> entities) {
        for (Item entity : entities) {
            addEntity(entity);
        }
    }

    @Override
    public Item getEntity(int id) {

    }

    @Override
    public ArrayList<Item> getAllEntities(@Nullable String column, int value) {
        synchronized (dbHelper) {
            ArrayList<Item> list = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String[] projection = { DatabaseSpec.ItemDB.FIELD_PKEY, DatabaseSpec.ItemDB.FIELD_NOTE_ID,
                    DatabaseSpec.ItemDB.FIELD_CONTENT, DatabaseSpec.ItemDB.FIELD_CHECKED,
                    DatabaseSpec.ItemDB.FIELD_INDEX };

            String selection = null;
            String[] selectionArgs = null;

            if (column != null) {
                if (column.equals(DatabaseSpec.ItemDB.FIELD_NOTE_ID)) {
                    selection = DatabaseSpec.ItemDB.FIELD_NOTE_ID + " = ?";
                }

                selectionArgs = new String[] { String.valueOf(value) };
            }

            Cursor cursor =
                    db.query(DatabaseSpec.ItemDB.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToNext()) {
                do {
                    Item item = new Item(cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_PKEY)),
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
            values.put(DatabaseSpec.ItemDB.FIELD_NOTE_ID, item.getNoteId());
            values.put(DatabaseSpec.ItemDB.FIELD_CONTENT, item.getContent());
            values.put(DatabaseSpec.ItemDB.FIELD_CHECKED, item.isChecked() ? 1 : 0);
            values.put(DatabaseSpec.ItemDB.FIELD_INDEX, item.getIndex());

            String selection = DatabaseSpec.ItemDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = { String.valueOf(item.getId()) };

            db.update(DatabaseSpec.ItemDB.TABLE_NAME, values, selection, selectionArgs);
        }
    }

    @Override
    public void deleteEntity(Item item) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = DatabaseSpec.ItemDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = { String.valueOf(item.getId()) };

            db.delete(DatabaseSpec.ItemDB.TABLE_NAME, selection, selectionArgs);
        }
    }

    public int deleteItemsByNoteId(int noteId) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = DatabaseSpec.ItemDB.FIELD_NOTE_ID + " = ?";
            String[] selectionArgs = { String.valueOf(noteId) };

            return db.delete(DatabaseSpec.ItemDB.TABLE_NAME, selection, selectionArgs);
        }
    }

    public ArrayList<Item> getAllItemsByNoteId(int noteId) {
        return getAllEntities(DatabaseSpec.ItemDB.FIELD_NOTE_ID, noteId);
    }

    public String getFullContent(int noteId, int action) {
        ArrayList<Item> items = getAllItemsByNoteId(noteId);
        String content = "";
        for (Item item : items) {
            String itemContent = item.getContent();
            if (action == Common.GET_ITEM_CONTENT_ACTION_LIST) {
                if (!itemContent.equals(""))
                content += "\u2022" + itemContent + "  ";
            } else if (action == Common.GET_ITEM_CONTENT_ACTION_SEND) {
                content += "[" + (item.isChecked() ? "x" : "") + "]" + " " + itemContent + "\n";
            }
        }

        return content;
    }

    @SuppressWarnings("unused")
    public int countConditionalItem(int noteId, boolean isChecked) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            int checked = isChecked ? 1 : 0;
            String queryString = "select count(*) from " + DatabaseSpec.ItemDB.TABLE_NAME + " where " +
                    DatabaseSpec.ItemDB.FIELD_NOTE_ID + " = " + noteId + " and " + DatabaseSpec.ItemDB.FIELD_CHECKED +
                    " = " + checked;

            Cursor mCount = db.rawQuery(queryString, null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();

            return count;
        }
    }

    @SuppressWarnings("unused")
    public ArrayList<Item> getAllItems() {
        return getAllEntities(null, 0);
    }
}
