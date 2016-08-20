package com.stevedao.note.model;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public abstract class Entity {
    protected Object id;

    public Entity() {
        id = -1;
    }
    public Entity(Object id) {
        this.id = id;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
