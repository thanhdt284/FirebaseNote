package com.stevedao.note.model;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public class Item extends Entity{
    public static final int defaultValue = -1;
    private int noteId;
    private String content;
    private boolean checked;
    private int index;

    public Item(int noteId, String content, boolean checked, int index) {
        this.noteId = noteId;
        this.content = content;
        this.checked = checked;
        this.index = index;
    }

    public Item(int id, int noteId, String content, boolean checked, int index) {
        super(id);
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
}
