package com.stevedao.note.model;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public abstract class Entity {
    private int id;

    public Entity() {
        id = -1;
    }
    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
