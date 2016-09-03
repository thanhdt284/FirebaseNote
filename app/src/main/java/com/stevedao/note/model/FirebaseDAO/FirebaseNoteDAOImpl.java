package com.stevedao.note.model.FirebaseDAO;

import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.EntityDAO;
import com.stevedao.note.model.Note;
import com.stevedao.note.model.SQLiteDAO.DatabaseSpec;
import com.stevedao.note.model.SQLiteDAO.SQLiteNoteDAOImpl;
import com.stevedao.note.view.ITaskResponse;

/**
 * Created by sev_user on 8/17/2016.
 *
 */
public class FirebaseNoteDAOImpl implements EntityDAO<Note> {

    private ITaskResponse mInterface;

    public FirebaseNoteDAOImpl() {
    }

    @Override
    public Object addEntity(Note note) {
        String noteKey = note.getFirebaseId();
        if (FirebaseUtil.getCurrentUser() != null) {
            DatabaseReference noteRef = FirebaseUtil.getNoteRef();

            if (noteRef != null) {
                if (noteKey.equals("")){
                    noteKey = noteRef.push().getKey();
                    note.setFirebaseId(noteKey);
                }

                noteRef.child(noteKey).updateChildren(note.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onComplete: Add note Error " + databaseError
                                    .getMessage());
                        }
                    }
                });
            }
        }

        return noteKey;
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
            Log.w(Common.APPTAG, "FirebaseNoteDAOImpl - addEntities: size = " + entities.size() + " - added = " + count);
            Log.w(Common.APPTAG, "FirebaseNoteDAOImpl - addEntities: some notes not added !!!");
        }

        return count;
    }

    @Override
    public Note getEntity(Object id) {
        final Note[] note = { null };
        if (FirebaseUtil.getCurrentUser() != null) {
            DatabaseReference noteRef = FirebaseUtil.getNoteRef();

            if (noteRef != null) {
                noteRef.child((String) id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        note[0] = getNoteFromSnapshot(dataSnapshot);

                        //TO-DO need to use interface to return data here
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onCancelled: get note error: " + databaseError.getMessage());
                    }
                });
            }
        }

        return note[0]; // do not return data here
    }

    @Override
    public ArrayList<Note> getAllEntities(String column, final Object value) {
        final ArrayList<Note> noteList = new ArrayList<>();
        if (FirebaseUtil.getCurrentUser() != null) {
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
                                noteList.add(getNoteFromSnapshot(snapshot));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onCancelled: getAllEntities (note) error : " + databaseError.getMessage());
                        }
                    });
                } else {
                    noteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                noteList.add(getNoteFromSnapshot(snapshot));
                            }

                            Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onDataChange: ");
                            if (value instanceof Integer) {
                                int tag = (int) value;

                                if (tag == 0) {
                                    Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onDataChange: onResponse");
                                    mInterface.onResponse(Common.LOGIN_NOTE_SYNC, noteList);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onCancelled: getAllEntities all note error : " + databaseError.getMessage());
                        }
                    });
                }
            }
        }

        //  Dummy code for later implementation
        //  noteList (size = 0)
        //  Need to return value after getting all data from Firebase Server
        return noteList;
    }

    @Override
    public void updateEntity(Note note) {
        if (FirebaseUtil.getCurrentUser() != null) {
            DatabaseReference noteRef = FirebaseUtil.getNoteRef();

            if (noteRef != null) {
                Map<String, Object> noteData = note.toMap();
                noteRef.child(note.getFirebaseId()).updateChildren(noteData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onComplete: update note error: " + databaseError.getMessage());
                        }
                    }
                });
            }
        }
    }

    @Override
    public void deleteEntity(Note note) {
        if (FirebaseUtil.getCurrentUser() != null) {
            DatabaseReference noteRef = FirebaseUtil.getNoteRef();

            if (noteRef != null) {
                noteRef.child("" + note.getFirebaseId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e(Common.APPTAG, "FirebaseNoteDAOImpl - onComplete: Delete note error: " + databaseError.getMessage());
                        }
                    }
                });
            }
        }
    }

    public void setInterface(ITaskResponse anInterface) {
        this.mInterface = anInterface;
    }

    private Note getNoteFromSnapshot(DataSnapshot snapshot) {
        int id = ((Long) snapshot.child(DatabaseSpec.NoteDB.FIELD_PKEY).getValue()).intValue();
        String firebaseId = snapshot.getKey();
        int color = ((Long) snapshot.child(DatabaseSpec.NoteDB.FIELD_COLOR).getValue()).intValue();
        String title = (String) snapshot.child(DatabaseSpec.NoteDB.FIELD_TITLE).getValue();
        int storageMode = ((Long) snapshot.child(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE).getValue
                ()).intValue();
        boolean isDone = (boolean) snapshot.child(DatabaseSpec.NoteDB.FIELD_IS_DONE).getValue();
        long deletedTime = (long) snapshot.child(DatabaseSpec.NoteDB.FIELD_DELETED_TIME).getValue();
        long lastModified = (long) snapshot.child(DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED)
                .getValue();

        return new Note(id, firebaseId, title, color, isDone, storageMode, lastModified,deletedTime);
    }
}
