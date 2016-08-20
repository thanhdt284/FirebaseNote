package com.stevedao.note.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.stevedao.note.control.Common;
import com.stevedao.note.model.FirebaseDAO.FirebaseNoteDAOImpl;
import com.stevedao.note.view.ITaskResponse;

import java.util.ArrayList;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class NoteDAOImpl implements EntityDAO<Note> {
    private static final String TAG = "NoteDAOImpl";
    private final DatabaseOpenHelper dbHelper;
    private FirebaseNoteDAOImpl fbNoteDAOImpl;

    private static NoteDAOImpl noteDAO;

    public static synchronized NoteDAOImpl getInstance(Context context) {
        if (noteDAO == null) {
            noteDAO = new NoteDAOImpl(context);
        }

        return noteDAO;
    }

    private NoteDAOImpl(Context context) {
        dbHelper = DatabaseOpenHelper.getInstance(context);
        fbNoteDAOImpl = new FirebaseNoteDAOImpl();
    }

    public void setFirebaseInterface(ITaskResponse iInterface) {
        fbNoteDAOImpl.setInterface(iInterface);
    }

    @Override
    public Object addEntity(Note note) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseSpec.NoteDB.FIELD_TITLE, note.getTitle());
            values.put(DatabaseSpec.NoteDB.FIELD_COLOR, note.getColor());
            values.put(DatabaseSpec.NoteDB.FIELD_IS_DONE, note.isDone() ? 1 : 0);
            values.put(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE, note.getStorageMode());
            values.put(DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED, note.getLastModified());
            values.put(DatabaseSpec.NoteDB.FIELD_DELETED_TIME, note.getDeletedTime());

            int insertedId = (int) db.insert(DatabaseSpec.NoteDB.TABLE_NAME, null, values);
            if (insertedId == -1) {
                Log.e(TAG, "addEntity: Add note error: insertedId = -1");
            } else {
                note.setId(insertedId);

                if (FirebaseUtil.getCurrentUser() != null) {
                    fbNoteDAOImpl.addEntity(note);
                }
            }
            return insertedId;
        }
    }

    @Override
    public int addEntities(ArrayList<Note> entities) {
        synchronized (dbHelper) {
            int count = 0;

            for (Note note : entities) {
                if (((Integer)addEntity(note)) >= 0) {
                    count++;
                }
            }

            if (FirebaseUtil.getCurrentUser() != null) {
                fbNoteDAOImpl.addEntities(entities);
            }

            return count;
        }
    }

    @Override
    public Note getEntity(Object id) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Note note = null;

            String[] projection = {
                    DatabaseSpec.NoteDB.FIELD_PKEY,
                    DatabaseSpec.NoteDB.FIELD_TITLE,
                    DatabaseSpec.NoteDB.FIELD_COLOR,
                    DatabaseSpec.NoteDB.FIELD_IS_DONE,
                    DatabaseSpec.NoteDB.FIELD_STORAGE_MODE,
                    DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED,
                    DatabaseSpec.NoteDB.FIELD_DELETED_TIME
            };
            String selection = DatabaseSpec.NoteDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = {
                    String.valueOf(id)
            };

            Cursor cursor = db.query(DatabaseSpec.NoteDB.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                note = new Note(cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_PKEY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_TITLE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_COLOR)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_IS_DONE)) > 0,
                        cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_DELETED_TIME)));

                cursor.close();
            }
            return note;
        }
    }

    @Override
    public ArrayList<Note> getAllEntities(@Nullable String column, Object value) {
        synchronized (dbHelper) {
            ArrayList<Note> list = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String[] projection = {
                    DatabaseSpec.NoteDB.FIELD_PKEY,
                    DatabaseSpec.NoteDB.FIELD_TITLE,
                    DatabaseSpec.NoteDB.FIELD_COLOR,
                    DatabaseSpec.NoteDB.FIELD_IS_DONE,
                    DatabaseSpec.NoteDB.FIELD_STORAGE_MODE,
                    DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED,
                    DatabaseSpec.NoteDB.FIELD_DELETED_TIME
            };

            String selection = null;
            String[] selectionArgs = null;
            String orderBy = DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED + " DESC";

            if (column != null) {
                if (column.equals(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE)) {
                    selection = DatabaseSpec.NoteDB.FIELD_STORAGE_MODE + " = ?";
                } else if (column.equals(DatabaseSpec.NoteDB.FIELD_COLOR)) {
                    selection = DatabaseSpec.NoteDB.FIELD_COLOR + " = ?";
                } else if (column.equals(DatabaseSpec.NoteDB.FIELD_IS_DONE)) {
                    selection = DatabaseSpec.NoteDB.FIELD_IS_DONE + " = ?";
                }

                selectionArgs = new String[]{String.valueOf(value)};
            }


            Cursor cursor = db.query(DatabaseSpec.NoteDB.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_PKEY));
                    String title = cursor.getString(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_TITLE));
                    int color = cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_COLOR));
                    boolean isDone = cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_IS_DONE)) > 0;
                    int storageMode = cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE));
                    long lastModified = cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED));
                    long deletedTime = cursor.getInt(cursor.getColumnIndex(DatabaseSpec.NoteDB.FIELD_DELETED_TIME));
                    list.add(new Note(id, title, color, isDone, storageMode, lastModified, deletedTime));
                } while (cursor.moveToNext());

                cursor.close();
            }

            return list;
        }
    }

    @Override
    public void updateEntity(Note note) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseSpec.NoteDB.FIELD_TITLE, note.getTitle());
            values.put(DatabaseSpec.NoteDB.FIELD_COLOR, note.getColor());
            values.put(DatabaseSpec.NoteDB.FIELD_IS_DONE, note.isDone() ? 1 : 0);
            values.put(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE, note.getStorageMode());
            values.put(DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED, note.getLastModified());
            values.put(DatabaseSpec.NoteDB.FIELD_DELETED_TIME, note.getDeletedTime());

            String selection = DatabaseSpec.NoteDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = {
                    String.valueOf(note.getId())
            };

            db.update(DatabaseSpec.NoteDB.TABLE_NAME, values, selection, selectionArgs);

            if (FirebaseUtil.getCurrentUser() != null) {
                fbNoteDAOImpl.updateEntity(note);
            }
        }
    }

    @Override
    public void deleteEntity(Note note) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = DatabaseSpec.NoteDB.FIELD_PKEY + " = ?";
            String[] selectionArgs = {
                    String.valueOf(note.getId())
            };

            db.delete(DatabaseSpec.NoteDB.TABLE_NAME, selection, selectionArgs);

            if (FirebaseUtil.getCurrentUser() != null) {
                fbNoteDAOImpl.deleteEntity(note);
            }
        }
    }

    public ArrayList<Note> getAllLocalNotes() {
        return getAllEntities(null, 0);
    }

    public ArrayList<Note> getAllServerNotes() {
        return fbNoteDAOImpl.getAllEntities();
    }

    public ArrayList<Note> getAllNotesByStorageMode(int storageMode) {
        return getAllEntities(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE, storageMode);
    }

    @SuppressWarnings("unused")
    public ArrayList<Note> getAllNotesByColor(int color) {
        return getAllEntities(DatabaseSpec.NoteDB.FIELD_COLOR, color);
    }

    @SuppressWarnings("unused")
    public ArrayList<Note> getAllNotesByIsDone(boolean isDone) {
        return getAllEntities(DatabaseSpec.NoteDB.FIELD_IS_DONE, isDone ? 1 : 0);
    }

    public void moveNoteToTrash(Note note) {
        note.setStorageMode(Common.NOTE_STORAGE_MODE_TRASH);
        updateEntity(note);
    }

    public void moveNoteToArchive(Note note) {
        note.setStorageMode(Common.NOTE_STORAGE_MODE_ARCHIVE);
        updateEntity(note);
    }

    public void moveNoteToActive(Note note) {
        note.setStorageMode(Common.NOTE_STORAGE_MODE_ACTIVE);
        updateEntity(note);
    }

//    public boolean isDataSynchronized() {
//        boolean isSynced = true;
//        ArrayList<Note> localNote = new ArrayList<>();
//        ArrayList<Note> serverNote = new ArrayList<>();
//
//        localNote = getAllEntities();
//        serverNote = fbNoteDAOImpl.getAllEntities();
//
//        int localSize = localNote.size();
//        int serverSize = serverNote.size();
//
//        if (localSize != serverSize) {
//            return false;
//        } else {
//            for (int i = 0; i < localSize; i++) {
//
//            }
//        }
//
//    }
}
