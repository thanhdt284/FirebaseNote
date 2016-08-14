package com.stevedao.note.model;

import java.util.Date;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */

public abstract class Note extends Entity {

    public static final String ROOT = "notes";
    public static final String TITLE = "title";
    public static final String COLOR = "COLOR";
    public static final String IS_DONE = "is_done";
    public static final String STORAGE_MODE = "storage_mode";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String DELETED_TIME = "deleted_time";
    public static final String FULL_CONTENT = "full_content";

    private final static int DEFAULT_COLOR = 7;
    private String title;
    private int color;
    private boolean isDone;
    private int storageMode;
    private long lastModified;
    private long deletedTime;

    public Note() {
        this.title = "";
        this.color = DEFAULT_COLOR;
        this.isDone = false;
        this.storageMode = 0;
        Date current = new Date();
        this.lastModified = current.getTime();
        this.deletedTime = 0;
    }

    @SuppressWarnings("unused")
    public Note(String title, int color, boolean isDone, int storageMode, long lastModified) {
        this.title = title;
        this.color = color;
        this.isDone = isDone;
        this.storageMode = storageMode;
        this.lastModified = lastModified;
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

    public void setIsDone(boolean done) {
        isDone = done;
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

}
