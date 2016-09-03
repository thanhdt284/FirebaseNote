package com.stevedao.note.model;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public abstract class Entity {
    protected Object id;
    protected String firebaseId;  // this is for synchronizing with firebase database

    public Entity() {
        id = -1;
        firebaseId = "";
    }
    public Entity(Object id) {
        this.id = id;
    }

    public Entity(Object id, String firebaseId) {
        this.id = id;
        this.firebaseId = firebaseId;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
