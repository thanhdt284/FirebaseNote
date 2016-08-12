package com.stevedao.note.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stevedao.note.control.Common;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class NoteDAOImpl implements EntityDAO<Note> {

    private static final String TAG = "MpteDAOImpl";
    private Context mContext;

    public NoteDAOImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public String addEntity(Note note) {
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();
        String noteKey = "";

        if (noteRef != null) {
            noteKey = noteRef.push().getKey();

            noteRef.child(noteKey).setValue(note, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: Add note Error " + databaseError.getMessage());
                    }
                }
            });
        }

        return noteKey;
    }

    public int addEntities(ArrayList<Note> entities) {
        int count = 0;

        for (Note note : entities) {
            if (!addEntity(note).equals("")) {
                count++;
            }
        }

        return count;
    }

    @Override
    public Note getEntity(String key) {
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();
        final Note[] note = { null };

        if (noteRef != null) {
            noteRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    note[0] = dataSnapshot.getValue(Note.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: get note error: " + databaseError.getMessage());
                }
            });
        }

        return note[0];
    }

    @Override
    public ArrayList<Note> getAllEntities(@Nullable String column, Object value) {
        final ArrayList<Note> noteList = new ArrayList<>();
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();

        if (noteRef != null) {
            Query query = null;

            if (column != null) {
                switch (column) {
                case FirebaseUtil.Note.FIELD_COLOR:
                case FirebaseUtil.Note.FIELD_STORAGE_MODE:
                    query = noteRef.orderByChild(column).equalTo((Integer) value);
                    break;
                case FirebaseUtil.Note.FIELD_IS_DONE:
                    query = noteRef.orderByChild(column).equalTo((Boolean) value);
                    break;
                default:
                    break;
                }
            }

            if (query != null) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            noteList.add(snapshot.getValue(Note.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: getAllEntities query error : " + databaseError.getMessage());
                    }
                });
            } else {
                noteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            noteList.add(snapshot.getValue(Note.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: getAllEntities all note error : " + databaseError.getMessage());
                    }
                });
            }
        }

        return noteList;
    }

    @Override
    public void updateEntity(Note note) {

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
        }
    }

    @SuppressWarnings("unused")
    public ArrayList<Note> getAllEntities() {
        return getAllEntities(null, 0);
    }

    public ArrayList<Note> getAllNotesByStorageMode(int storageMode) {
        return getAllEntities(FirebaseUtil.Note.FIELD_STORAGE_MODE, storageMode);
    }

    @SuppressWarnings("unused")
    public ArrayList<Note> getAllNotesByColor(int color) {
        return getAllEntities(FirebaseUtil.Note.FIELD_COLOR, color);
    }

    @SuppressWarnings("unused")
    public ArrayList<Note> getAllNotesByIsDone(boolean isDone) {
        return getAllEntities(FirebaseUtil.Note.FIELD_IS_DONE, isDone);
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
}
