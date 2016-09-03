package com.stevedao.note.model;

import android.content.Context;
import com.stevedao.note.model.FirebaseDAO.FirebaseUserDAOImpl;
import com.stevedao.note.model.SQLiteDAO.SQLiteUserDAOImpl;

/**
 * Created by sev_user on 8/23/2016.
 *
 */
public class UserDAOManager {

    private final SQLiteUserDAOImpl sqliteUserDAO;
    private final FirebaseUserDAOImpl firebaseUserDAO;

    public UserDAOManager(Context context) {
        sqliteUserDAO = new SQLiteUserDAOImpl(context);
        firebaseUserDAO = new FirebaseUserDAOImpl();
    }

    public void addUser(User user) {
        firebaseUserDAO.addEntity(user);
        sqliteUserDAO.addEntity(user);
    }

    public void deleteUser() {
        sqliteUserDAO.deleteUserData();
    }

}
