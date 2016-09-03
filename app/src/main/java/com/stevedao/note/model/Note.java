package com.stevedao.note.model;

import java.util.HashMap;
import java.util.Map;
import com.stevedao.note.model.SQLiteDAO.DatabaseSpec;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */

public class Note extends Entity {
    private String title;
    private int color;
    private boolean isDone;
    private int storageMode;
    private long lastModified;
    private long deletedTime;
    private String fullContent;

    public Note() { }

    @SuppressWarnings("unused")
    public Note(String title, int color, boolean isDone, int storageMode, long lastModified, long deletedTime) {
        this.title = title;
        this.color = color;
        this.isDone = isDone;
        this.storageMode = storageMode;
        this.lastModified = lastModified;
        this.deletedTime = deletedTime;
    }

    public Note(int id, String title, int color, boolean isDone, int storageMode, long lastModified, long deletedTime) {
        super(id);
        this.title = title;
        this.color = color;
        this.isDone = isDone;
        this.storageMode = storageMode;
        this.lastModified = lastModified;
        this.deletedTime = deletedTime;
    }

    public Note(int id,String firebaseKey, String title, int color, boolean isDone, int storageMode, long lastModified,
                long
            deletedTime) {
        super(id, firebaseKey);
        this.title = title;
        this.color = color;
        this.isDone = isDone;
        this.storageMode = storageMode;
        this.lastModified = lastModified;
        this.deletedTime = deletedTime;
    }

    @Override
    public Integer getId() {
        return (Integer) super.getId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public int getStorageMode() {
        return storageMode;
    }

    public void setStorageMode(int storageMode) {
        this.storageMode = storageMode;
    }

    public long getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(long deletedTime) {
        this.deletedTime = deletedTime;
    }

    public String getFullContent() {
        return fullContent;
    }

    public void setFullContent(String fullContent) {
        this.fullContent = fullContent;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(DatabaseSpec.NoteDB.FIELD_PKEY, this.id);
        map.put(DatabaseSpec.NoteDB.FIELD_TITLE, this.title);
        map.put(DatabaseSpec.NoteDB.FIELD_COLOR, this.color);
        map.put(DatabaseSpec.NoteDB.FIELD_IS_DONE, this.isDone);
        map.put(DatabaseSpec.NoteDB.FIELD_STORAGE_MODE, this.storageMode);
        map.put(DatabaseSpec.NoteDB.FIELD_LAST_MODIFIED, this.lastModified);
        map.put(DatabaseSpec.NoteDB.FIELD_DELETED_TIME, this.deletedTime);

        return map;
    }

    public boolean isEqualTo(Note note) {
        return (this.id == note.getId() && this.title.equals(note.getTitle()) && this.color == note.getColor() &&
                this.isDone == note.isDone() && this.storageMode == note.getStorageMode() &&
                this.lastModified == note.getLastModified() && this.deletedTime == note.getDeletedTime());
    }
}
