package com.stevedao.note.model;

import java.util.ArrayList;
import android.content.Context;
import com.stevedao.note.model.FirebaseDAO.FirebaseNoteDAOImpl;
import com.stevedao.note.model.SQLiteDAO.DatabaseSpec;
import com.stevedao.note.model.SQLiteDAO.SQLiteNoteDAOImpl;
import com.stevedao.note.view.ITaskResponse;

/**
 * Created by sev_user on 8/23/2016.
 *
 */
public class NoteDAOManager {
    private FirebaseNoteDAOImpl firebaseNoteDAO;
    private SQLiteNoteDAOImpl sqliteNoteDAO;

    public NoteDAOManager(Context context) {
        firebaseNoteDAO = new FirebaseNoteDAOImpl();
        sqliteNoteDAO = new SQLiteNoteDAOImpl(context);
    }

    public void setInterface(ITaskResponse anInterface) {
        firebaseNoteDAO.setInterface(anInterface);
    }

    public void addNote(Note note) {
        String noteKey = (String) firebaseNoteDAO.addEntity(note);
        note.setFirebaseId(noteKey);

        sqliteNoteDAO.addEntity(note);
    }

    public void addNotes(ArrayList<Note> noteList, boolean isLocalOnly) {
        for (Note note : noteList) {
            String noteKey = note.getFirebaseId();

            if (!isLocalOnly) {
                noteKey = (String) firebaseNoteDAO.addEntity(note);
            }

            if (!noteKey.equals("")) {
                note.setFirebaseId(noteKey);
            }

            sqliteNoteDAO.addEntity(note);
        }
    }

    public Note getLocalNote(int noteId) {
        return sqliteNoteDAO.getEntity(noteId);
    }

    public void updateNote(Note note) {
        firebaseNoteDAO.updateEntity(note);
        sqliteNoteDAO.updateEntity(note);
    }

    public void deleteNote(Note note) {
        firebaseNoteDAO.deleteEntity(note);
        sqliteNoteDAO.deleteEntity(note);
    }

    public ArrayList<Note> getLocalNotes() {
        return sqliteNoteDAO.getAllEntities(null, 0);
    }

    public void changeStorageMode(Note note, int storageMode) {
        note.setStorageMode(storageMode);

        firebaseNoteDAO.updateEntity(note);
        sqliteNoteDAO.updateEntity(note);
    }

    public ArrayList<Note> getNotesByStorageMode(int storageMode) {
        return sqliteNoteDAO.getAllEntities(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE, storageMode);
    }

    public void uploadNotesData(ArrayList<Note> noteList) {
        for (Note note : noteList) {
            uploadNote(note);
        }
    }

    public String uploadNote(Note note) {
        String firebaseKey = (String) firebaseNoteDAO.addEntity(note);
        note.setFirebaseId(firebaseKey);
        sqliteNoteDAO.updateEntity(note);
        return firebaseKey;
    }

    public void deleteLocalData() {
        sqliteNoteDAO.deleteAllNoteData();
    }


    public ArrayList<Note> getServerData() {
        return firebaseNoteDAO.getAllEntities(null, 0);
    }
}
