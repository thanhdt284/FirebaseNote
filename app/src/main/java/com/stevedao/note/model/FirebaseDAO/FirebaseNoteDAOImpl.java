package com.stevedao.note.model.FirebaseDAO;

import java.util.ArrayList;
import java.util.Map;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.EntityDAO;
import com.stevedao.note.model.FirebaseUtil;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.DatabaseSpec;
import com.stevedao.note.view.ITaskResponse;

/**
 * Created by sev_user on 8/17/2016.
 *
 */
public class FirebaseNoteDAOImpl implements EntityDAO<Note> {

    private static final String TAG = "FirebaseNoteDAOImpl";
    private ITaskResponse mInterface;

    public FirebaseNoteDAOImpl() {

    }

    @Override
    public Object addEntity(Note note) {
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();

        if (noteRef != null) {
            noteRef.child("" + note.getId()).updateChildren(note.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: Add note Error " + databaseError.getMessage());
                    }
                }
            });
        }

        return 0;
    }

    @Override
    public int addEntities(ArrayList<Note> entities) {
        int count = 0;

        for (Note note : entities) {
            if (addEntity(note) == 0) {
                count++;
            }
        }

        if (count != entities.size()) {
            Log.w(TAG, "addEntities: size = " + entities.size() + " - added = " + count);
            Log.w(TAG, "addEntities: some notes not added !!!");
        }

        return count;
    }

    @Override
    public Note getEntity(Object id) {
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();
        final Note[] note = { null };

        if (noteRef != null) {
            noteRef.child((String) id).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public ArrayList<Note> getAllEntities(String column, final Object value) {
        final ArrayList<Note> noteList = new ArrayList<>();
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();

        if (noteRef != null) {
            Query query = null;

            if (column != null) {
                switch (column) {
                case DatabaseSpec.NoteDB.FIELD_COLOR:
                case DatabaseSpec.NoteDB.FIELD_STORAGE_MODE:
                    query = noteRef.orderByChild(column).equalTo((Integer) value);
                    break;
                case DatabaseSpec.NoteDB.FIELD_IS_DONE:
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
                        Log.e(TAG, "onCancelled: getAllEntities (note) error : " + databaseError.getMessage());
                    }
                });
            } else {
                noteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            noteList.add(snapshot.getValue(Note.class));
                        }
                        if (value instanceof Integer) {
                            int tag = (int) value;

                            if (tag == 0) {
                                mInterface.onResponse(Common.LOGIN_NOTE_SYNC, noteList);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: getAllEntities all note error : " + databaseError.getMessage());
                    }
                });
            }
        }

        //  Dummy code for later implementation
        //  noteList (size = 0)
        //  Need to return value after getting all data from Firebase Server
        return noteList;
    }

    @Override
    public void updateEntity(Note note) {
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();

        if (noteRef != null) {
            Map<String, Object> noteData = note.toMap();
            noteRef.child("" + note.getId()).updateChildren(noteData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: update note error: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void deleteEntity(Note note) {
        DatabaseReference noteRef = FirebaseUtil.getNoteRef();

        if (noteRef != null) {
            noteRef.child("" + note.getId()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "onComplete: Delete note error: " + databaseError.getMessage());
                    }
                }
            });
        }
    }

    public ArrayList<Note> getAllEntities() {
        return getAllEntities(null, 0); // 0 for login sync
    }

    public void setInterface(ITaskResponse anInterface) {
        this.mInterface = anInterface;
    }
}
