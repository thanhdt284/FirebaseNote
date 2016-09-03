package com.stevedao.note.model;

import java.util.HashMap;
import java.util.Map;
import com.stevedao.note.model.SQLiteDAO.DatabaseSpec;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class Item extends Entity {
    public static final int defaultValue = -1;
    private int noteId;
    private String firebaseNoteId;
    private String content;
    private boolean checked;
    private int index;

    @SuppressWarnings("unused")
    public Item() {
    } // use for firebase mapping

    public Item(String firebaseNoteId, int noteId, String content, boolean checked, int index) {
        this.firebaseNoteId = firebaseNoteId;
        this.noteId = noteId;
        this.content = content;
        this.checked = checked;
        this.index = index;
    }

    public Item(int id, String firebaseId, String firebaseNoteId, int noteId, String content, boolean checked, int
            index) {
        super(id, firebaseId);
        this.firebaseNoteId = firebaseNoteId;
        this.noteId = noteId;
        this.content = content;
        this.checked = checked;
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    @SuppressWarnings("unused")
    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(DatabaseSpec.ItemDB.FIELD_PKEY, this.id);
        map.put(DatabaseSpec.ItemDB.FIELD_FIREBASE_NOTE_KEY, this.firebaseNoteId);
        map.put(DatabaseSpec.ItemDB.FIELD_NOTE_ID, this.noteId);
        map.put(DatabaseSpec.ItemDB.FIELD_CONTENT, this.content);
        map.put(DatabaseSpec.ItemDB.FIELD_CHECKED, this.checked);
        map.put(DatabaseSpec.ItemDB.FIELD_INDEX, this.index);

        return map;
    }

    public boolean isEqualTo(Item item) {
        return (this.id == item.getId() && this.noteId == item.getNoteId() && this.index == item.getIndex() &&
                this.checked == item.isChecked() && this.content.equals(item.getContent()));
    }

    public String getFirebaseNoteId() {
        return firebaseNoteId;
    }

    public void setFirebaseNoteId(String firebaseNoteId) {
        this.firebaseNoteId = firebaseNoteId;
    }
}
