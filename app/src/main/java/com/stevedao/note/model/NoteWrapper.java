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
}
