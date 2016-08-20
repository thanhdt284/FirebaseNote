package com.stevedao.note.model;

/**
 * Created by thanh.dao on 12/08/2016.
 * Wrap Note object and manage full content of note
 */
public class NoteWrapper {

    private Note note;
    private String fullContent;

    public NoteWrapper(Note note) {
        this.note = note;
    }

    public String getFullContent() {
        return fullContent;
    }

    public void setFullContent(String fullContent) {
        this.fullContent = fullContent;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getId() {
        return note.getId();
    }

    public void setId(String id) {
        note.setId(id);
    }

    public String getTitle() {
        return note.getTitle();
    }

    public void setTitle(String title) {
        note.setTitle(title);
    }

    public int getColor() {
        return note.getColor();
    }

    public void setColor(int color) {
        note.setColor(color);
    }

    public Object getLastModified() {
        return note.getLastModified();
    }

    public void setLastModified(Object lastModified) {
        note.setLastModified(lastModified);
    }

    public boolean isDone() {
        return note.isDone();
    }

    public void setIsDone(boolean done) {
        note.setIsDone(done);
    }

    public int getStorageMode() {
        return note.getStorageMode();
    }

    public void setStorageMode(int storageMode) {
        note.setStorageMode(storageMode);
    }

    public Object getDeletedTime() {
        return note.getDeletedTime();
    }

    public void setDeletedTime(long deletedTime) {
        note.setDeletedTime(deletedTime);
    }
}
