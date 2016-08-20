package com.stevedao.note.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.FirebaseDAO.FirebaseItemDAOImpl;
import com.stevedao.note.view.ITaskResponse;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class ItemDAOImpl implements EntityDAO<Item> {
    private static final String TAG = "ItemDAOImpl";
    private final DatabaseOpenHelper dbHelper;
    private FirebaseItemDAOImpl firebaseItemDAO;

    private static ItemDAOImpl itemDAO;

    public static synchronized ItemDAOImpl getInstance(Context context) {
        if (itemDAO == null) {
            itemDAO = new ItemDAOImpl(context);
        }
        return itemDAO;
    }

    private ItemDAOImpl(Context context) {
        dbHelper = DatabaseOpenHelper.getInstance(context);
        firebaseItemDAO = new FirebaseItemDAOImpl();
    }

    public void setFirebaseInterface(ITaskResponse iInterface) {
        firebaseItemDAO.setInterface(iInterface);
    }

    @Override
    public Object addEntity(Item item) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseSpec.ItemDB.FIELD_NOTE_ID, item.getNoteId());
            values.put(DatabaseSpec.ItemDB.FIELD_CONTENT, item.getContent());
            values.put(DatabaseSpec.ItemDB.FIELD_CHECKED, item.isChecked() ? 1 : 0);
            values.put(DatabaseSpec.ItemDB.FIELD_INDEX, item.getIndex());

            int insertedId = (int) db.insert(DatabaseSpec.ItemDB.TABLE_NAME, null, values);
            if (insertedId >= 0) {
                item.setId(insertedId);

                if (FirebaseUtil.getCurrentUser() != null) {
                    firebaseItemDAO.addEntity(item);
                }
            } else {
                Log.e(TAG, "addEntity: Add item error: insertedId = -1");
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

            if (FirebaseUtil.getCurrentUser() != null) {
                firebaseItemDAO.addEntities(entities);
            }
            return count;
        }
    }

    @Override
    public Item getEntity(Object id) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Item item = null;

            String[] projection = { DatabaseSpec.ItemDB.FIELD_PKEY, DatabaseSpec.ItemDB.FIELD_NOTE_ID,
                    DatabaseSpec.ItemDB.FIELD_CONTENT, DatabaseSpec.ItemDB.FIELD_CHECKED,
                    DatabaseSpec.ItemDB.FIELD_INDEX };
            String selection = DatabaseSpec.ItemDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            Cursor cursor =
                    db.query(DatabaseSpec.ItemDB.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                item = new Item(cursor.getInt(cursor.getColumnIndex(DatabaseSpec.ItemDB.FIELD_PKEY)),
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

            if (FirebaseUtil.getCurrentUser() != null) {
                firebaseItemDAO.updateEntity(item);
            }
        }
    }

    @Override
    public void deleteEntity(Item item) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = DatabaseSpec.ItemDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = { String.valueOf(item.getId()) };

            db.delete(DatabaseSpec.ItemDB.TABLE_NAME, selection, selectionArgs);

            if (FirebaseUtil.getCurrentUser() != null) {
                firebaseItemDAO.deleteEntity(item);
            }
        }
    }

    public int deleteItemsByNoteId(int noteId) {
        synchronized (dbHelper) {
            if (FirebaseUtil.getCurrentUser() != null) {
                firebaseItemDAO.deleteItemsByNoteId(noteId);
            }

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

    public ArrayList<Item> getAllLocalItems() {
        return getAllEntities(null, 0);
    }

    public ArrayList<Item> getAllServerItems() {
        return firebaseItemDAO.getAllEntities();
    }
}
