package com.stevedao.note.model;

import java.util.ArrayList;

/**
 * Created by thanh.dao on 07/04/2016.
 *
 */
public interface EntityDAO<T extends Entity> {
    Object addEntity(T t);

    int addEntities(ArrayList<T> entities);

    T getEntity(Object id);

    ArrayList<T> getAllEntities(String column, Object value);

    void updateEntity(T t);

    void deleteEntity(T t);
}
