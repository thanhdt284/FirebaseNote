package com.stevedao.note.model.SQLiteDAO;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.EntityDAO;
import com.stevedao.note.model.User;

/**
 * Created by sev_user on 8/17/2016.
 *
 */
public class SQLiteUserDAOImpl implements EntityDAO<User> {
    private final DatabaseOpenHelper dbHelper;

    public SQLiteUserDAOImpl(Context mContext) {
        dbHelper = DatabaseOpenHelper.getInstance(mContext);
    }

    @Override
    public Object addEntity(User user) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseSpec.UserDB.FIELD_PKEY, (String) user.getId());
            values.put(DatabaseSpec.UserDB.FIELD_DISPLAY_NAME, user.getDisplayName());
            values.put(DatabaseSpec.UserDB.FIELD_EMAIL, user.getEmail());
            values.put(DatabaseSpec.UserDB.FIELD_PHOTO_URL, user.getPhotoUrl());

            if (db.insert(DatabaseSpec.UserDB.TABLE_NAME, null, values) < 0) {
                Log.e(Common.APPTAG, "SQLiteUserDAOImpl - addEntity: Add new user error");
            }

            return 0;
        }
    }

    @Override
    public int addEntities(ArrayList<User> entities) {
        synchronized (dbHelper) {
            for (User user : entities) {
                addEntity(user);
            }
        }
        return 0;
    }

    @Override
    public User getEntity(Object id) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            User user = null;

            String[] projection = {
                    DatabaseSpec.UserDB.FIELD_PKEY,
                    DatabaseSpec.UserDB.FIELD_DISPLAY_NAME,
                    DatabaseSpec.UserDB.FIELD_EMAIL,
                    DatabaseSpec.UserDB.FIELD_PHOTO_URL
            };
            String selection = DatabaseSpec.UserDB.FIELD_PKEY + " = ?";
            String[] selctionArgs = {String.valueOf(id)};

            Cursor cursor =
                    db.query(DatabaseSpec.UserDB.TABLE_NAME, projection, selection, selctionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new User(cursor.getString(cursor.getColumnIndex(DatabaseSpec.UserDB.FIELD_PKEY)),
                                cursor.getString(cursor.getColumnIndex(DatabaseSpec.UserDB.FIELD_DISPLAY_NAME)),
                                cursor.getString(cursor.getColumnIndex(DatabaseSpec.UserDB.FIELD_EMAIL)),
                                cursor.getString(cursor.getColumnIndex(DatabaseSpec.UserDB.FIELD_PHOTO_URL)));
                cursor.close();
            }

            return user;
        }
    }

    @Override
    public ArrayList<User> getAllEntities(String column, Object value) {
        synchronized (dbHelper) {
            ArrayList<User> userList = new ArrayList<>();

            userList.add(getEntity(value));

            return userList;
        }
    }

    @Override
    public void updateEntity(User user) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseSpec.UserDB.FIELD_DISPLAY_NAME, user.getDisplayName());
            values.put(DatabaseSpec.UserDB.FIELD_EMAIL, user.getEmail());
            values.put(DatabaseSpec.UserDB.FIELD_PHOTO_URL, user.getPhotoUrl());

            String whereClause = DatabaseSpec.UserDB.FIELD_PKEY + " = ?";

            String[] whereArgs = {String.valueOf(user.getId())};
            db.update(DatabaseSpec.UserDB.TABLE_NAME, values, whereClause, whereArgs);
        }
    }

    @Override
    public void deleteEntity(User user) {
        synchronized (dbHelper) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String whereClause = DatabaseSpec.UserDB.FIELD_PKEY + " = ?";
            String[] whereArgs = {String.valueOf(user.getId())};

            db.delete(DatabaseSpec.UserDB.TABLE_NAME, whereClause, whereArgs);
        }
    }

    public void deleteUserData() {
        synchronized (dbHelper) {
            String queryString = "DELETE FROM " + DatabaseSpec.UserDB.TABLE_NAME;
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.rawQuery(queryString, null);
        }
    }
}
